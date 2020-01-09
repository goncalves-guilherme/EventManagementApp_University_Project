package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import com.silent_manager.g29.silent_manager.data_layer.models.Token

interface IAccountService : IService {
    fun login(email: String, password: String, onSuccess: (Token?) -> Unit, onError: (RequestError) -> Unit)
    fun registerUser(
        name: String,
        email: String,
        password: String,
        onSuccess: (Token?) -> Unit,
        onError: (RequestError) -> Unit
    )
    fun logout()
}