package com.silent_manager.g29.silent_manager.android_components.cache

import android.arch.persistence.room.Room

class LocalCache private constructor() {
    companion object{
        private val localCache by lazy {
            LocalCache()
        }

        fun getInstance()= localCache
    }

    private val db by lazy {
        //Room.databaseBuilder(this, SilentManagerDatabase::class.java, "silentmanager-db").build()
    }
}