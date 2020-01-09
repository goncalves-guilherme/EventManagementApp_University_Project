package com.silent_manager.g29.silent_manager.android_components.view_models

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.managers.TokenManager
import com.silent_manager.g29.silent_manager.data_layer.models.*
import com.silent_manager.g29.silent_manager.data_layer.request.ErrorInterpreter

class EventsCreatedViewModel(applicationContext: SilentManagerApplication) :
    AndroidViewModel(applicationContext) {
    private val _usersAcceptedByEvent: ArrayList<Pair<Int, Array<User>>> by lazy {
        arrayListOf<Pair<Int, Array<User>>>()
    }
    private val _invitedAcceptedUsersByEventList: MutableLiveData<List<Pair<Int, Array<User>>>> by lazy {
        MutableLiveData<List<Pair<Int, Array<User>>>>()
    }
    private val _usersPendingByEvent: ArrayList<Pair<Int, Array<User>>> by lazy {
        arrayListOf<Pair<Int, Array<User>>>()
    }
    private val _invitedPendingUsersByEventList: MutableLiveData<List<Pair<Int, Array<User>>>> by lazy {
        MutableLiveData<List<Pair<Int, Array<User>>>>()
    }

    fun getInvitedPendingUsersByEventLiveData(): MutableLiveData<List<Pair<Int, Array<User>>>> =
        _invitedPendingUsersByEventList

    fun getInvitedAcceptedUsersByEventLiveData(): MutableLiveData<List<Pair<Int, Array<User>>>> =
        _invitedAcceptedUsersByEventList

    private val _createdEvents: MutableLiveData<MutableList<Event>> by lazy { MutableLiveData<MutableList<Event>>() }
    fun getCreatedEvents(): LiveData<MutableList<Event>?> = _createdEvents

    fun inviteUser(eventId: Int, userId: Int) {
        val token = getAccessToken()
        getApplication<SilentManagerApplication>().invitationService.inviteUser(
            token,
            eventId,
            userId,
            ::onInviteUserSucceed,
            ::onInviteUserFailed
        )
    }

    fun updateInvitationsByEvent(eventId: Int) {
        val accessToken = getAccessToken()
        getApplication<SilentManagerApplication>().invitationService.getInvites(
            accessToken,
            eventId,
            null,
            ::onInvitationsReceivedByEvent,
            ::onInvitationsError
        )
    }

    fun deleteEvent(eventId: Int) {
        val token = getAccessToken()
        getApplication<SilentManagerApplication>().eventService.deleteEvent(
            eventId,
            token, ::onDeleteSucceed,
            ::onDeleteError
        )
    }

    fun deleteInvite(eventId: Int, userId: Int) {
        val token = getAccessToken()
        getApplication<SilentManagerApplication>().invitationService.deleteInvite(
            token,
            eventId,
            userId,
            ::onDeleteSucceed,
            ::onDeleteError
        )
    }

    private fun onDeleteSucceed(succeed: Boolean?) {}
    private fun onDeleteError(error: RequestError) {}

    private fun onInvitationsReceivedByEvent(invitations: Array<Invite>?) {
        if (invitations.isNullOrEmpty()) {
            return
        }

        if (invitations.isNullOrEmpty()) {
            return
        }

        val eventId = invitations.first().event?.eventId

        val acceptedInvitations = invitations.filter { it.state == InviteState.ACCEPTED_INVITE }
        val acceptedUsers = acceptedInvitations.mapNotNull { it.user }.toTypedArray()

        val pendingInvitations = invitations.filter { it.state == InviteState.PENDING_INVITE }
        val pendingUsers = pendingInvitations.mapNotNull { it.user }.toTypedArray()

        if (eventId != null) {
            _usersAcceptedByEvent.add(Pair(eventId, acceptedUsers))
            _usersPendingByEvent.add(Pair(eventId, pendingUsers))

            _invitedAcceptedUsersByEventList.value = _usersAcceptedByEvent
            _invitedPendingUsersByEventList.value = _usersPendingByEvent
        }
    }

    private fun onInvitationsError(error: RequestError) {
    }


    private fun onInviteUserSucceed(invite: Invite?) {
        invite?.let { i ->
            if (i.event?.eventId != null && i.user != null) {
                val eventPair = _usersPendingByEvent.find { it.first == i.event.eventId }
                if (eventPair != null) {
                    val newUsers = eventPair.second.toMutableList()
                    newUsers.add(i.user)

                    _usersPendingByEvent.remove(eventPair)
                    _usersPendingByEvent.add(Pair(i.event.eventId, newUsers.toTypedArray()))

                    _invitedPendingUsersByEventList.value = _usersPendingByEvent

                    return
                }

                val newUsers = mutableListOf<User>()
                newUsers.add(i.user)
                _usersPendingByEvent.add(Pair(i.event.eventId, newUsers.toTypedArray()))
                _invitedPendingUsersByEventList.value = _usersPendingByEvent
            }
        }
    }


    private fun onInviteUserFailed(error: RequestError) {
    }

    fun updateEvents() {
        val token = getAccessToken()
        getApplication<SilentManagerApplication>().eventService.getCreatedEvents(
            token,
            ::onUpdateEventsSucceed,
            ::onFailedRequest
        )
    }

    private fun getAccessToken(): Token {
        val tm = TokenManager.getInstance()
        return tm.getToken(getApplication<SilentManagerApplication>().applicationContext)
    }

    private fun onFailedRequest(error: RequestError) {
        if (error.code == ErrorInterpreter.ErrorCode.NEED_AUNTHENTICATION_ERROR) {
            val tm = TokenManager.getInstance()
            tm.invalidateToken(getApplication())
        }
        _createdEvents.value = null
    }

    private fun onUpdateEventsSucceed(result: PageResult<Event>?) {
        result?.results?.let {
            _createdEvents.value = it.toMutableList()
        }
    }
}