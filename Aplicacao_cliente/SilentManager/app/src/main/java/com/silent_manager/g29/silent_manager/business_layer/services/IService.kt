package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import com.silent_manager.g29.silent_manager.data_layer.models.Response

interface IService {
    fun <T> processResult(response: Response<T>, onSuccess: (T?) -> Unit, onError: (RequestError) -> Unit)
}