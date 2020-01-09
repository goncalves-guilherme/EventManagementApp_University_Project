package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.*

interface IInvitationService : IService {
    fun getInvites(
        token: Token,
        eventId: Int?,
        inviteState: Int?,
        onSuccess: (Array<Invite>?) -> Unit,
        onError: (RequestError) -> Unit
    )

    fun inviteUser(
        token: Token,
        eventId: Int,
        userId: Int,
        onSuccess: (Invite?) -> Unit,
        onError: (RequestError) -> Unit
    )

    fun acceptInvite(
        token: Token,
        invite: Invite,
        onSuccess: (Invite?) -> Unit,
        onError: (RequestError) -> Unit
    )

    fun rejectInvite(
        token: Token,
        invite: Invite,
        onSuccess: (Invite?) -> Unit,
        onError: (RequestError) -> Unit
    )

    fun deleteInvite(
        token: Token, eventId: Int, userId: Int, onSuccess: (Boolean?) -> Unit,
        onError: (RequestError) -> Unit
    )
}