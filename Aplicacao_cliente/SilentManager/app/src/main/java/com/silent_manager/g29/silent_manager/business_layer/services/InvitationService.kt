package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.*
import com.silent_manager.g29.silent_manager.data_layer.repository.IInviteRepository

class InvitationService(private val inviteRepo: IInviteRepository) : Service(), IInvitationService {
    override fun inviteUser(
        token: Token,
        eventId: Int,
        userId: Int,
        onSuccess: (Invite?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val event: Event = Event(
            eventId = eventId,
            name = null,
            state = null,
            radius = null,
            location = null,
            endDate = null,
            startDate = null,
            description = null,
            author = null,
            category = ""
        )
        val user: User = User(name = null, id = userId, email = null)
        val invite: Invite = Invite(user = user, event = event, state = InviteState.PENDING_INVITE)
        inviteRepo.putInviteChange(token, invite) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun getInvites(
        token: Token,
        eventId: Int?,
        inviteState: Int?,
        onSuccess: (Array<Invite>?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        if (eventId != null) {
            inviteRepo.getInvitesByEvent(token, eventId, inviteState) {
                processResult(it, onSuccess, onError)
            }
            return
        }
        inviteRepo.getInvites(token, inviteState) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun acceptInvite(
        token: Token,
        invite: Invite,
        onSuccess: (Invite?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val toUpdateInvite =
            Invite(event = invite.event, user = invite.user, state = InviteState.ACCEPTED_INVITE)

        inviteRepo.putInviteChange(token, toUpdateInvite) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun rejectInvite(
        token: Token,
        invite: Invite,
        onSuccess: (Invite?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val toUpdateInvite =
            Invite(event = invite.event, user = invite.user, state = InviteState.REJECTED_INVITE)

        inviteRepo.putInviteChange(token, toUpdateInvite) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun deleteInvite(
        token: Token,
        eventId: Int,
        userId: Int,
        onSuccess: (Boolean?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        inviteRepo.deleteInvite(token, eventId, userId){
            processResult(it, onSuccess, onError)
        }
    }
}