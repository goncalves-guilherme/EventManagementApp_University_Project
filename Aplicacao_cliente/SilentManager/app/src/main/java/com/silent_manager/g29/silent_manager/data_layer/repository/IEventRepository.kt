package com.silent_manager.g29.silent_manager.data_layer.repository

import com.silent_manager.g29.silent_manager.data_layer.models.*

interface IEventRepository : IRepository {
    fun getEvents(parameters: List<Pair<String, String>>, cb: (Response<PageResult<Event>>) -> Unit)
    fun putEvent(newEvent: Event, token: Token, cb: (Response<Event>) -> Unit)
    fun removeEvent(eventId: Int, token: Token, cb: (Response<Boolean>) -> Unit)
    fun getEventsCreatedByUser(paramenterTokens: List<Pair<String, String>>, token: Token, cb: (Response<PageResult<Event>>) -> Unit)
}