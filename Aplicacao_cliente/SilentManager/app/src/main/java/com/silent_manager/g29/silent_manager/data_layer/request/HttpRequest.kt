package com.silent_manager.g29.silent_manager.data_layer.request

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import com.silent_manager.g29.silent_manager.data_layer.models.Response as SilentManagerResponse

class HttpRequest(
    private val requestQueue: RequestQueue,
    private val errorInterpreter: ErrorInterpreter
) : IRequest {
    override fun delete(
        url: String,
        accessToken: String?,
        data: JSONObject,
        callback: (com.silent_manager.g29.silent_manager.data_layer.models.Response<String>) -> Unit
    ) {
        makeRequest(Request.Method.DELETE, url, accessToken, data, callback)
    }

    override fun delete(
        url: String,
        accessToken: String?,
        callback: (com.silent_manager.g29.silent_manager.data_layer.models.Response<String>) -> Unit
    ) {
        makeRequest(Request.Method.DELETE, url, accessToken, callback)
    }

    override fun put(
        url: String,
        accessToken: String?,
        data: JSONObject,
        callback: (com.silent_manager.g29.silent_manager.data_layer.models.Response<String>) -> Unit
    ) {
        makeRequest(Request.Method.PUT, url, accessToken, data, callback)
    }

    override fun put(
        url: String,
        data: JSONObject,
        callback: (com.silent_manager.g29.silent_manager.data_layer.models.Response<String>) -> Unit
    ) {
        makeRequest(Request.Method.PUT, url, null, data, callback)
    }

    private fun makeRequest(
        httpMethod: Int, url: String, accessToken: String?, data: JSONObject?,
        callback: (com.silent_manager.g29.silent_manager.data_layer.models.Response<String>) -> Unit
    ) {
        val stringRequest = object : JsonObjectRequest(
            httpMethod, url, data,
            Response.Listener<JSONObject> {
                callback(SilentManagerResponse(it.toString()))
            },
            Response.ErrorListener {
                val error = errorInterpreter.convertToApplicationError(it?.networkResponse?.statusCode)
                callback(SilentManagerResponse(error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = mutableMapOf<String, String>()

                if(!accessToken.isNullOrEmpty())
                    headers["Token"] = accessToken

                return headers
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun makeRequest(httpMethod: Int, url: String, accessToken: String?,
                            callback: (com.silent_manager.g29.silent_manager.data_layer.models.Response<String>) -> Unit){
        val stringRequest = object : StringRequest(
            httpMethod,
            url,
            Response.Listener<String> {
                callback(SilentManagerResponse(it))
            },
            Response.ErrorListener {
                val error = errorInterpreter.convertToApplicationError(it?.networkResponse?.statusCode)
                callback(SilentManagerResponse(error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = mutableMapOf<String, String>()

                if(!accessToken.isNullOrEmpty())
                    headers["Token"] = accessToken

                return headers
            }
        }

        requestQueue.add(stringRequest)
    }

    override fun post(
        url: String, data: JSONObject, callback: (SilentManagerResponse<String>) -> Unit
    ) {
        makeRequest(Request.Method.POST, url, null, data, callback)
    }

    override fun get(url: String, callback: (SilentManagerResponse<String>) -> Unit) {
        get(url, null, callback)
    }

    override fun get(
        url: String,
        token: String?,
        callback: (com.silent_manager.g29.silent_manager.data_layer.models.Response<String>) -> Unit
    ) {
        makeRequest(Request.Method.GET, url, token, callback)
    }

}

