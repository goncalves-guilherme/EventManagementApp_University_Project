package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.models.*

/**
 * This is the contract used by the data layer and services to obtain specific data about users
 * in this application.
 *
 * */
interface IUserRepository : IRepository {

    /**
     * This method is used to obtain a user by its eventId.
     *
     * @param id, The user eventId.
     * @param cb, The callback to be called when the request is finished. This function receives the
     * user return by a specific api.
     * */
    fun getUser(id: Int, cb: (Response<User>) -> Unit)

    fun getUsersByParameters(parameters: List<Pair<String, String>>, cb: (Response<PageResult<User>>) -> Unit)
}