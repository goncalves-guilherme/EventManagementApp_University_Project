package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.models.*
import com.silent_manager.g29.silent_manager.data_layer.request.IRequest

/**
 * Repository used by the services to obtain data about users.
 * */
class UserRepository(request: IRequest, host: String) : Repository(request, host), IUserRepository {
    companion object {
        const val USER_END_POINT: String = "/api/v1/users"
    }
    val USER_REPO_TAG = "UserRepository"

    /**
     * This method will obtain a user by its eventId
     *
     * @params eventId
     * */
    override fun getUser(id: Int, cb: (Response<User>) -> Unit) {
        request.get("$HOST_API$USER_END_POINT/$id")
        {
            cb(dispatchJsonMessage(it))
        }
    }

    override fun getUsersByParameters(
        parameterTokens: List<Pair<String, String>>, cb: (Response<PageResult<User>>) -> Unit
    ) {
        val parameters = parseParametersToUrl(parameterTokens)
        // Obtain all users who satisfy the list of filters defined by parameters.
        request.get("$HOST_API$USER_END_POINT?$parameters")
        {
            cb(dispatchJsonMessage(it))
        }
    }
}