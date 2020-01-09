package com.silent_manager.g29.silent_manager.data_layer.request

import com.silent_manager.g29.silent_manager.data_layer.models.RequestError

interface IErrorInterpreter {
    val errorMessageProvider: IErrorMessageProvider
    fun convertToApplicationError(httpCode: Int?): RequestError
}