package com.silent_manager.g29.silent_manager.data_layer.request

interface IErrorMessageProvider {
    fun getErrorMessage(errorCode: ErrorInterpreter.ErrorCode): String
}