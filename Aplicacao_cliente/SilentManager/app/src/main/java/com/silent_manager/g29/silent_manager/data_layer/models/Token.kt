package com.silent_manager.g29.silent_manager.data_layer.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Token(
    @JsonProperty("accessToken")
    val AccessToken: String?,
    @JsonProperty("expiration")
    val Expiration: String?
)