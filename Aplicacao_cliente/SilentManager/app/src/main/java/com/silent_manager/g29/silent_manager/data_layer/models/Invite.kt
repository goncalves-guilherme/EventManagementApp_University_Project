package com.silent_manager.g29.silent_manager.data_layer.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Invite(
    @JsonProperty("user")
    val user: User?,
    @JsonProperty("event")
    val event: Event?,
    @JsonProperty("state")
    val state: Int?
)