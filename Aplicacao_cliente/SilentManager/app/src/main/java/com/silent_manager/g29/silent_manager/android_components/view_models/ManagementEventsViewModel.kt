package com.silent_manager.g29.silent_manager.android_components.view_models

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.managers.TokenManager
import com.silent_manager.g29.silent_manager.data_layer.models.*


class ManagementEventsViewModel(applicationContext: SilentManagerApplication) :
    AndroidViewModel(applicationContext) {
    private val _invitationsLiveData: MutableLiveData<Array<Invite>> by lazy {
        MutableLiveData<Array<Invite>>()
    }

    private val _acceptedUsersByEvent: ArrayList<Pair<Int, Array<User>>> by lazy {
        arrayListOf<Pair<Int, Array<User>>>()
    }

    private val _acceptedInvitedUsersByEventList: MutableLiveData<List<Pair<Int, Array<User>>>> by lazy {
        MutableLiveData<List<Pair<Int, Array<User>>>>()
    }

    private val _pendingUsersByEvent: ArrayList<Pair<Int, Array<User>>> by lazy {
        arrayListOf<Pair<Int, Array<User>>>()
    }

    private val _pendingInvitedUsersByEventList: MutableLiveData<List<Pair<Int, Array<User>>>> by lazy {
        MutableLiveData<List<Pair<Int, Array<User>>>>()
    }

    fun getInvitationsLiveData(): LiveData<Array<Invite>?> = _invitationsLiveData

    fun getAcceptedInvitedUsersByEventLiveData(): MutableLiveData<List<Pair<Int, Array<User>>>> =
        _acceptedInvitedUsersByEventList

    fun getPendingInvitedUsersByEventLiveData(): MutableLiveData<List<Pair<Int, Array<User>>>> =
        _pendingInvitedUsersByEventList

    fun updateInvitationsByEvent(eventId: Int) {
        val accessToken = getAccessToken()
        getApplication<SilentManagerApplication>().invitationService.getInvites(
            accessToken,
            eventId,
            null,
            ::invitationsReceivedByEvent,
            ::onInvitationsError
        )
    }

    private fun invitationsReceivedByEvent(invitations: Array<Invite>?) {
        if (invitations.isNullOrEmpty()) {
            return
        }

        val eventId = invitations.first().event?.eventId

        val acceptedInvitations = invitations.filter { it.state == InviteState.ACCEPTED_INVITE }
        val acceptedUsers = acceptedInvitations.mapNotNull { it.user }.toTypedArray()

        val pendingInvitations = invitations.filter { it.state == InviteState.PENDING_INVITE }
        val pendingUsers = pendingInvitations.mapNotNull { it.user }.toTypedArray()

        if (eventId != null) {
            _acceptedUsersByEvent.add(Pair(eventId, acceptedUsers))
            _pendingUsersByEvent.add(Pair(eventId, pendingUsers))

            _acceptedInvitedUsersByEventList.value = _acceptedUsersByEvent
            _pendingInvitedUsersByEventList.value = _pendingUsersByEvent

        } else {
            _acceptedInvitedUsersByEventList.value = null
            _pendingInvitedUsersByEventList.value = null
        }

    }

    fun updateAcceptedInvitations() {
        val accessToken = getAccessToken()
        getApplication<SilentManagerApplication>().invitationService.getInvites(
            accessToken,
            null,
            InviteState.ACCEPTED_INVITE,
            ::onAcceptedInvitationsReceived,
            ::onInvitationsError
        )
    }

    private fun onAcceptedInvitationsReceived(invitations: Array<Invite>?) {
        _invitationsLiveData.value = invitations
    }

    private fun onInvitationsError(error: RequestError) {

    }


    fun updatePendingUserInvitations() {
        val accessToken = getAccessToken()
        getApplication<SilentManagerApplication>().invitationService.getInvites(
            accessToken,
            null,
            InviteState.PENDING_INVITE,
            ::onPendingInvitationsReceived,
            ::onPendingInvitationsError
        )
    }

    fun acceptInvitation(invite: Invite) {
        val accessToken = getAccessToken()
        getApplication<SilentManagerApplication>().invitationService.acceptInvite(
            accessToken,
            invite,
            {},
            {})
    }

    fun rejectInvitation(invite: Invite) {
        val accessToken = getAccessToken()
        getApplication<SilentManagerApplication>().invitationService.rejectInvite(
            accessToken,
            invite,
            {},
            {})
    }

    private fun onPendingInvitationsReceived(invitations: Array<Invite>?) {
        if (invitations != null)
            _invitationsLiveData.value = invitations
        else
            _invitationsLiveData.value = null
    }

    private fun onPendingInvitationsError(error: RequestError) {
        val i = 0
    }

    private fun getAccessToken(): Token {
        val tm = TokenManager.getInstance()
        return tm.getToken(getApplication<SilentManagerApplication>().applicationContext)
    }

    fun logout() {
        val tm = TokenManager.getInstance()
        tm.invalidateToken(getApplication<SilentManagerApplication>().applicationContext)
    }
}