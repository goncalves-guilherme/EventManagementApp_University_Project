package com.silent_manager.g29.silent_manager.data_layer

import com.silent_manager.g29.silent_manager.data_layer.repository.IEventRepository
import com.silent_manager.g29.silent_manager.data_layer.repository.IUserRepository

/**
 * This interface represents the contract between the Data layer and the services.
 *
 * */
interface IUnitOfWork {
    /**
     * This repository can obtain data about users or a specific user.
     * */
    val userRepo: IUserRepository
    val eventRepo: IEventRepository
}