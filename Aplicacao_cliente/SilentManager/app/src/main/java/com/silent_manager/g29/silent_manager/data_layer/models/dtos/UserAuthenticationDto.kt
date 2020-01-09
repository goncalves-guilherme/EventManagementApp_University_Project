package com.silent_manager.g29.silent_manager.data_layer.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class UserAuthenticationDto(
    @JsonProperty("Email")
    val Email: String,
    @JsonProperty("Password")
    val Password: String
)