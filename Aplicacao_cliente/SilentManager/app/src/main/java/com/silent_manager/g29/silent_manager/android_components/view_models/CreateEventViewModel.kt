package com.silent_manager.g29.silent_manager.android_components.view_models

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.android_components.managers.TokenManager
import com.silent_manager.g29.silent_manager.data_layer.models.*
import com.silent_manager.g29.silent_manager.data_layer.request.ErrorInterpreter
import java.util.*

class CreateEventViewModel(applicationContext: SilentManagerApplication) : AndroidViewModel(applicationContext) {
    private val _createdEvent: MutableLiveData<Event?> = MutableLiveData()

    fun createdEvent(): LiveData<Event?> = _createdEvent

    fun createEvent(
        eventId: Int?,
        name: String?,
        desc: String?,
        startDate: Date?,
        endDate: Date?,
        latitude: Double?,
        longitude: Double?,
        radius: Int?,
        category: String?
    ) {
        val location = Location(latitude = latitude, longitude = longitude, address = null, id = null)
        val accessToken = getAccessToken()
        val event = Event(
            name = name, description = desc, location = location,
            startDate = startDate, endDate = endDate, radius = radius,
            eventId = eventId, state = EventState.CREATED.ordinal, author = null, category = category)

        getApplication<SilentManagerApplication>().eventService.createEvent(
            event,
            accessToken,
            ::onUpdateEventSucceed,
            ::onFailedRequest
        )
    }

    private fun getAccessToken(): Token {
        val tm = TokenManager.getInstance()
        return tm.getToken(getApplication<SilentManagerApplication>().applicationContext)
    }

    private fun onFailedRequest(error: RequestError) {
        if (error.code == ErrorInterpreter.ErrorCode.NEED_AUNTHENTICATION_ERROR) {
            val tm = TokenManager.getInstance()
            tm.invalidateToken(getApplication())
        }
        _createdEvent.value = null
    }

    private fun onUpdateEventSucceed(event: Event?) {
        event?.let {
            _createdEvent.value = it
        }
    }
}