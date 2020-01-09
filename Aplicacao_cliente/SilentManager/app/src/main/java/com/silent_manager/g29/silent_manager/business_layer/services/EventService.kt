package com.silent_manager.g29.silent_manager.business_layer.services

import com.silent_manager.g29.silent_manager.data_layer.models.*
import com.silent_manager.g29.silent_manager.data_layer.repository.IEventRepository

class EventService(private val eventRepo: IEventRepository) : Service(), IEventService {
    override fun getCreatedEvents(
        token: Token,
        onSuccess: (PageResult<Event>?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val parameters: MutableList<Pair<String, String>> = mutableListOf()
        eventRepo.getEventsCreatedByUser(parameters, token){
            processResult(it, onSuccess, onError)
        }
    }

    override fun deleteEvent(
        eventId: Int,
        token: Token,
        onSuccess: (Boolean?) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        eventRepo.removeEvent(eventId, token) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun editEvent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createEvent(event: Event, token: Token, onSuccess: (Event?) -> Unit, onError: (RequestError) -> Unit) {

        eventRepo.putEvent(event, token) {
            processResult(it, onSuccess, onError)
        }
    }

    override fun getEvents(
        latitude: Double,
        longitude: Double,
        radius: Int,
        category: String?,
        onSuccess: (PageResult<Event>?) -> Unit,
        onError: (RequestError) -> Unit
    ) {

        val parameters: MutableList<Pair<String, String>> = mutableListOf()

        parameters.add(Pair("latitude", latitude.toString()))
        parameters.add(Pair("longitude", longitude.toString()))
        parameters.add(Pair("radius", radius.toString()))

        if (!category.isNullOrBlank())
            parameters.add(Pair("category", category))

        eventRepo.getEvents(parameters) {
            processResult(it, onSuccess, onError)
        }
    }

}