package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.models.*

interface IInviteRepository {
    fun putInviteChange(token: Token, invite: Invite, cb: (Response<Invite>) -> Unit)
    fun postInviteUsers(
        token: Token,
        eventId: Int,
        users: Array<User>,
        cb: (Response<Event>) -> Unit
    )

    fun getInvites(token: Token, state: Int?, cb: (Response<Array<Invite>>) -> Unit)
    fun getInvitesByEvent(
        token: Token,
        eventId: Int,
        state: Int?,
        cb: (Response<Array<Invite>>) -> Unit
    )

    fun deleteInvite(token: Token, eventId: Int, userId: Int, cb: (Response<Boolean>) -> Unit)
}