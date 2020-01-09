package com.silent_manager.g29.silent_manager.data_layer.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class EventOutDto(
    @JsonProperty("EventId")
    val eventId: Int?,
    @JsonProperty("Name")
    val name: String?,
    @JsonProperty("Description")
    val description: String?,
    @JsonProperty("StartDate")
    val startDate: String,
    @JsonProperty("EndDate")
    val endDate: String,
    @JsonProperty("Location")
    val location: Location?,
    @JsonProperty("Radius")
    val radius: Int?,
    @JsonProperty("State")
    val state: Int?,
    @JsonProperty("Category")
    val category: String?
) {
    companion object {
        fun transformEventToDto(event: Event): EventOutDto {
            val df = DateFormat.getDateTimeInstance()
            val startDate = df.format(event.startDate)
            val endDate = df.format(event.endDate)
            return EventOutDto(
                eventId = event.eventId,
                name = event.name,
                description = event.description,
                startDate = startDate,
                endDate = endDate,
                location = event.location,
                radius = event.radius,
                state = event.state,
                category = event.category
                )
        }
    }
}