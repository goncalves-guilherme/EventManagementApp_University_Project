package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.RegisterUser
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import com.silent_manager.g29.silent_manager.data_layer.models.Token
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.UserAuthenticationDto
import com.silent_manager.g29.silent_manager.data_layer.repository.IAccountRepository

class AccountService(private val accountRepo: IAccountRepository) : Service(), IAccountService {
    override fun registerUser(
        name: String,
        email: String,
        password: String,
        onSuccess: (Token?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val userToRegister = RegisterUser(name, password, email)
        accountRepo.createUser(userToRegister) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun login(
        email: String,
        password: String,
        onSuccess: (Token?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val userToAuthenticate =
            UserAuthenticationDto(email, password)

        accountRepo.authenticateUser(userToAuthenticate) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun logout() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}