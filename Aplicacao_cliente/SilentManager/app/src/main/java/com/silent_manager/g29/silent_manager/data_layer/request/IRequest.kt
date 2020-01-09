package com.silent_manager.g29.silent_manager.data_layer.request

import com.silent_manager.g29.silent_manager.data_layer.models.Response
import org.json.JSONObject

/**
 * Contract used by the repositories to make requests.
 * */
interface IRequest {
    /**
     * This method is used to create a resource in the path url.
     *
     * @param url, represents the url of the resource to be created.
     *
     * */
    fun post(url: String, data: JSONObject, callback: (Response<String>) -> Unit)

    fun put(url: String, data: JSONObject, callback: (Response<String>) -> Unit)
    fun put(
        url: String,
        accessToken: String?,
        data: JSONObject,
        callback: (Response<String>) -> Unit
    )

    fun delete(url: String, accessToken: String?, callback: (Response<String>) -> Unit)
    fun delete(
        url: String,
        accessToken: String?,
        data: JSONObject,
        callback: (Response<String>) -> Unit
    )

    /**
     * This method gets a specific resource from a url and call the callback when the request is returned.
     *
     * @param url, The location of the resource,
     * @param callback, The callback to be called when the request is returnerd.
     * */
    fun get(url: String, callback: (Response<String>) -> Unit)

    fun get(url: String, token: String?, callback: (Response<String>) -> Unit)
}