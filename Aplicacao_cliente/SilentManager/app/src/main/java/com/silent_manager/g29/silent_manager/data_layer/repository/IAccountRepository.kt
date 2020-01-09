package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.models.RegisterUser
import com.silent_manager.g29.silent_manager.data_layer.models.Response
import com.silent_manager.g29.silent_manager.data_layer.models.Token
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.UserAuthenticationDto

interface IAccountRepository{
    /**
     * This method will create a new account.
     * */
    fun createUser(user: RegisterUser, cb: (Response<Token>) -> Unit)

    fun authenticateUser(user: UserAuthenticationDto, cb: (Response<Token>) -> Unit)

    fun logout(token: Token, cb: (Response<Boolean>) -> Unit)
}
