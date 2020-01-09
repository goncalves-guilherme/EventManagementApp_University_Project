package com.silent_manager.g29.silent_manager.data_layer.models

import com.silent_manager.g29.silent_manager.data_layer.request.ErrorInterpreter


data class RequestError(
    val code: ErrorInterpreter.ErrorCode,
    val message: String
)