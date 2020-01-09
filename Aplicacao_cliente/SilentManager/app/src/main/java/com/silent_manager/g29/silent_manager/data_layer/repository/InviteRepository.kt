package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.models.*
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.InviteDto
import com.silent_manager.g29.silent_manager.data_layer.request.IRequest

class InviteRepository(request: IRequest, host: String) : Repository(request, host), IInviteRepository {
    companion object {
        const val INVITES_POINT = "/api/v1/invites"
        const val MY_INVITES = "/users/me"
        const val INVITES_BY_EVENT = "/events/"
        private const val STATE_PARAMETER = "State"
    }

    override fun getInvitesByEvent(
        token: Token,
        eventId: Int,
        state: Int?,
        cb: (Response<Array<Invite>>) -> Unit
    ) {
        var parameters = ""

        state?.let {
            parameters += "$STATE_PARAMETER=$state"
        }

        request.get(
            "$HOST_API$INVITES_POINT$INVITES_BY_EVENT$eventId?$parameters",
            token.AccessToken
        ) {
            cb(dispatchJsonMessage(it))
        }
    }

    override fun putInviteChange(token: Token, invite: Invite, cb: (Response<Invite>) -> Unit) {
        val inviteJson = entityToJSON(InviteDto.convertToDto(invite))

        request.put("$HOST_API$INVITES_POINT", token.AccessToken, inviteJson) {
            cb(dispatchJsonMessage(it))
        }
    }

    override fun postInviteUsers(
        token: Token,
        eventId: Int,
        users: Array<User>,
        cb: (Response<Event>) -> Unit
    ) {
        val tokenJson = entityToJSON(token).toString()
        val usersListJson = entityToJSON(users)

        /*
        request.post("$HOST_API$INVITES_POINT", tokenJson, usersListJson) {
            cb(dispatchJsonMessage(it))
        }*/
    }

    override fun getInvites(token: Token, state: Int?, cb: (Response<Array<Invite>>) -> Unit) {
        var parameters = ""

        state?.let {
            parameters += "$STATE_PARAMETER=$state"
        }

        request.get("$HOST_API$INVITES_POINT$MY_INVITES?$parameters", token.AccessToken) {
            cb(dispatchJsonMessage(it))
        }
    }

    override fun deleteInvite(
        token: Token,
        eventId: Int,
        userId: Int,
        cb: (Response<Boolean>) -> Unit
    ) {
        request.delete("$HOST_API$INVITES_POINT/event/$eventId/user/$userId", token.AccessToken) {
            cb(dispatchJsonMessage(it))
        }
    }
}