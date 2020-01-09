package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.*

interface IEventService : IService {
    fun getEvents(
        latitude: Double,
        longitude: Double,
        radius: Int,
        category: String?,
        onSuccess: (PageResult<Event>?) -> Unit,
        onError: (RequestError) -> Unit
    )

    fun createEvent(
        event: Event,
        token: Token,
        onSuccess: (Event?) -> Unit,
        onError: (RequestError) -> Unit
    )

    fun getCreatedEvents(token: Token, onSuccess: (PageResult<Event>?) -> Unit, onError: (RequestError) -> Unit)
    fun editEvent()
    fun deleteEvent(
        eventId: Int,
        token: Token,
        onSuccess: (Boolean?) -> Unit,
        onError: (RequestError) -> Unit
    )
}