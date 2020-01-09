package com.silent_manager.g29.silent_manager.data_layer

import com.silent_manager.g29.silent_manager.data_layer.repository.IEventRepository
import com.silent_manager.g29.silent_manager.data_layer.repository.IUserRepository

/**
 * This is an implementation of IUnitOfWork which contains a collection of repositories used by the services to get
 * data a specific data.
 *
 * */
class UnitOfWork(
    /**
     * User repository to obtain data about a list or a single user.
     * */
    override val userRepo: IUserRepository,
    override val eventRepo: IEventRepository
) : IUnitOfWork