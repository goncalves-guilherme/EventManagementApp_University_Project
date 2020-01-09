package com.silent_manager.g29.silent_manager.data_layer.models

data class Response<T>(
    val result: T?,
    val error: RequestError?
){
    constructor(result: T?) : this(result, null)
    constructor(error: RequestError?) : this(null, error)
}