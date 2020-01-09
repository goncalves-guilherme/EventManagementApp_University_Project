package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.models.RegisterUser
import com.silent_manager.g29.silent_manager.data_layer.models.Response
import com.silent_manager.g29.silent_manager.data_layer.models.Token
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.TokenDto
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.UserAuthenticationDto
import com.silent_manager.g29.silent_manager.data_layer.request.IRequest

class AccountRepository(request: IRequest, host: String) : Repository(request, host), IAccountRepository {
    companion object {
        const val LOGIN_END_POINT: String = "/api/v1/login"
        const val REGISTER_END_POINT: String = "/api/v1/register"
        const val LOGOUT_END_POINT: String = "/api/v1/logout"
    }
    override fun createUser(user: RegisterUser, cb: (Response<Token>) -> Unit) {
        val userJson = entityToJSON(user)

        request.post("$HOST_API$REGISTER_END_POINT", userJson){
            cb(dispatchJsonMessage(it))
        }
    }

    override fun authenticateUser(user: UserAuthenticationDto, cb: (Response<Token>) -> Unit) {
        val userJson = entityToJSON(user)

        request.post("$HOST_API$LOGIN_END_POINT", userJson){
            cb(dispatchJsonMessage(it))
        }
    }

    override fun logout(token: Token, cb: (Response<Boolean>) -> Unit) {
    }

}