package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.*
import com.silent_manager.g29.silent_manager.data_layer.repository.IUserRepository
import com.silent_manager.g29.silent_manager.data_layer.request.ErrorInterpreter

class UserService(private val userRepo: IUserRepository) : Service(), IUserService {

    override fun getUserById(id: Int, onSuccess: (User?) -> Unit, onError: (RequestError) -> Unit) {
        userRepo.getUser(id) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun getUserByParameter(query: String, onSuccess: (PageResult<User>?) -> Unit, onError: (RequestError) -> Unit) {

        if (query.isNullOrBlank()) {
            val invalidQuery = "Your query is invalid"
            val response: Response<PageResult<User>?> =
                Response(null, RequestError(ErrorInterpreter.ErrorCode.UNKNOWN_ERROR, invalidQuery))
            processResult(response, onSuccess, onError)
            return
        }

        val queryParameter: String = findUserQueryToken(query)
        val parameters: List<Pair<String, String>> = listOf(Pair(queryParameter, query))

        userRepo.getUsersByParameters(parameters) {
            processResult(it, onSuccess, onError)
        }
    }

    private fun findUserQueryToken(query: String): String {
        return when (query.contains('@')) {
            true -> "email"
            false -> "username"
        }
    }
}