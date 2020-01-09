package com.silent_manager.g29.silent_manager.data_layer.models.dtos

import com.silent_manager.g29.silent_manager.data_layer.models.Invite

data class InviteDto(
    val userId: Int?,
    val eventId: Int?,
    val state: Int?
) {
    companion object {
        fun convertToDto(invite: Invite): InviteDto {
            return InviteDto(userId = invite.user?.id, eventId = invite.event?.eventId, state = invite.state)
        }
    }
}