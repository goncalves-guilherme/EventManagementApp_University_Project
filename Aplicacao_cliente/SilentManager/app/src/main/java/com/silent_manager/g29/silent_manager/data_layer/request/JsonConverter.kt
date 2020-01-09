package com.silent_manager.g29.silent_manager.data_layer.request

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.reflect.KClass

/**
 * Static object to mapping a json string to a kClass
 * */
object JsonConverter {
    val mapper: ObjectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun <T : Any> convert(str: String, kclass: KClass<T>): T = mapper.readValue(str, kclass.java)
    inline fun <reified T : Any> convert(str: String): T = mapper.readValue(str)
    fun <T : Any> convert(str: String, ref: TypeReference<T>): T = mapper.readValue(str, ref)

    fun <T : Any> convertToJson(data: T): String = mapper.writeValueAsString(data)
}