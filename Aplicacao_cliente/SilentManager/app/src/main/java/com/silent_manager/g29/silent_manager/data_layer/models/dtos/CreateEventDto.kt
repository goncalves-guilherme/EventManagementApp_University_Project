package com.silent_manager.g29.silent_manager.data_layer.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.silent_manager.g29.silent_manager.data_layer.models.Token

data class CreateEventDto(
    @JsonProperty("eventDto")
    val event: EventOutDto,
    @JsonProperty("token")
    val token: TokenDto
)