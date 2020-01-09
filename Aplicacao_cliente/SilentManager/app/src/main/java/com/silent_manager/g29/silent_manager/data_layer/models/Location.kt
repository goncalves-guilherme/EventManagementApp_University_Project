package com.silent_manager.g29.silent_manager.data_layer.models

import android.arch.persistence.room.Entity
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@Entity(tableName = "Location", primaryKeys = arrayOf("id"))
data class Location(
    @JsonProperty("locationId")
    val id: Int?,
    @JsonProperty("latitude")
    val latitude: Double?,
    @JsonProperty("longitude")
    val longitude: Double?,
    @JsonProperty("address")
    val address: String?
) : Serializable