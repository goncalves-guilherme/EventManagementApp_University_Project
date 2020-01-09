package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.models.*
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.CreateEventDto
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.EventOutDto
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.TokenDto
import com.silent_manager.g29.silent_manager.data_layer.request.IRequest

/**
 * Repository used by the services to obtain data about users.
 * */
class EventRepository(request: IRequest, host: String) : Repository(request, host), IEventRepository {
    companion object {
        const val EVENT_END_POINT: String = "/api/v1/events"
        const val USER_EVENTS: String = "/users/me"
    }

    override fun putEvent(newEvent: Event, token: Token, cb: (Response<Event>) -> Unit) {
        val eventOutDto = EventOutDto.transformEventToDto(newEvent)
        val tokenDto = TokenDto.transformModelToDto(token)
        val event = CreateEventDto(event = eventOutDto, token = tokenDto)
        val eventJson = entityToJSON(event)

        request.put("$HOST_API$EVENT_END_POINT", eventJson) {
            cb(dispatchJsonMessage(it))
        }
    }

    override fun removeEvent(eventId: Int, token: Token, cb: (Response<Boolean>) -> Unit){
        request.delete("$HOST_API$EVENT_END_POINT/$eventId", token.AccessToken) {
            cb(dispatchJsonMessage(it))
        }
    }

    override fun getEventsCreatedByUser(
        paramenterTokens: List<Pair<String, String>>,
        token: Token,
        cb: (Response<PageResult<Event>>) -> Unit
    ) {
        val parameters = parseParametersToUrl(paramenterTokens)
        request.get("$HOST_API$EVENT_END_POINT$USER_EVENTS?$parameters", token.AccessToken) {
            cb(dispatchJsonMessage(it))
        }
    }

    override fun getEvents(paramenterTokens: List<Pair<String, String>>, cb: (Response<PageResult<Event>>) -> Unit) {
        val parameters = parseParametersToUrl(paramenterTokens)

        request.get("$HOST_API$EVENT_END_POINT?$parameters")
        {
            cb(dispatchJsonMessage(it))
        }
    }
}