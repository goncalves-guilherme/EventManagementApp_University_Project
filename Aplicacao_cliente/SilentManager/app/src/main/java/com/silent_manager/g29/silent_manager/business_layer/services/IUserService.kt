package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.PageResult
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import com.silent_manager.g29.silent_manager.data_layer.models.User

interface IUserService : IService {
    fun getUserById(id: Int, onSuccess: (User?) -> Unit, onError: (RequestError) -> Unit)
    fun getUserByParameter(query: String, onSuccess: (PageResult<User>?) -> Unit, onError: (RequestError) -> Unit)
}