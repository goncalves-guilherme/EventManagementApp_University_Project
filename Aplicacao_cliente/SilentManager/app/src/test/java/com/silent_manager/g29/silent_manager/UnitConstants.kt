package com.silent_manager.g29.silent_manager

import com.silent_manager.Request.FileRequest
import com.silent_manager.g29.silent_manager.data_layer.IUnitOfWork
import com.silent_manager.g29.silent_manager.data_layer.repository.EventRepository
import com.silent_manager.g29.silent_manager.data_layer.repository.UserRepository
import com.silent_manager.g29.silent_manager.data_layer.request.IRequest
import com.silent_manager.g29.silent_manager.data_layer.UnitOfWork

class UnitConstants{
    companion object {
        fun buildUnitOfWork(): IUnitOfWork {
            // This request will obtain data from a static file
            val fileRequest: IRequest = FileRequest()
            // Data Layer
            return UnitOfWork(eventRepo = EventRepository(fileRequest), userRepo = UserRepository(fileRequest))
        }
    }
}