package com.silent_manager.g29.silent_manager.data_layer.models

import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

@Entity(tableName = "Events", primaryKeys = arrayOf("eventId"))
data class Event(
    @JsonProperty("EventId")
    val eventId: Int?,
    @JsonProperty("Name")
    val name: String?,
    @JsonProperty("Description")
    val description: String?,
    @JsonProperty("StartDate")
    val startDate: Date?,
    @JsonProperty("EndDate")
    val endDate: Date?,
    @JsonProperty("Author")
    val author: User?,
    @JsonProperty("Location")
    val location: Location?,
    @JsonProperty("Radius")
    val radius: Int?,
    @JsonProperty("State")
    val state: Int?,
    @JsonProperty("Category")
    val category: String?
) : Serializable
