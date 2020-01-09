package com.silent_manager.g29.silent_manager.android_components.cache

import android.arch.persistence.room.*
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import com.silent_manager.g29.silent_manager.data_layer.models.User

@Dao
interface SilentManagerDAO {
    @Query("SELECT * FROM Events")
    fun getAllEvents(): Array<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvents(vararg events: Event)

    @Delete
    fun deleteEvents()
}

@Database(entities = arrayOf(Event::class, Location::class, User::class), version = 1)
abstract class SilentManagerDatabase : RoomDatabase() {
    abstract fun SilentManagerDAO(): SilentManagerDAO
}