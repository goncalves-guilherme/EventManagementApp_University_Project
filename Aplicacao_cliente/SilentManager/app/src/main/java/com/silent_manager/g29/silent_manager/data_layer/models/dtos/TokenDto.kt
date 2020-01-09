package com.silent_manager.g29.silent_manager.data_layer.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.silent_manager.g29.silent_manager.data_layer.models.Token

data class TokenDto(
    @JsonProperty("AccessToken")
    val AccessToken: String?,
    @JsonProperty("Expiration")
    val Expiration: String?
){
    companion object {
        fun transformModelToDto(token: Token): TokenDto {
            return TokenDto(
                AccessToken = token.AccessToken,
                Expiration = token.Expiration
            )
        }

        fun transformDtoToModel(tokenDto: Token): Token{
            return Token(
                AccessToken = tokenDto.AccessToken,
                Expiration = tokenDto.Expiration
            )
        }
    }
}