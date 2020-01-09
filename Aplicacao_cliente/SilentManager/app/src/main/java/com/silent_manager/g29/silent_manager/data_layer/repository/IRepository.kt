package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.request.IRequest

/**
 * IRepository interface represents a repository that will used a specific request to obtain data.
 * */
interface IRepository {
    val request: IRequest
}