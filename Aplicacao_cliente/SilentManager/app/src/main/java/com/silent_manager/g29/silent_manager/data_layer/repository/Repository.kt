package com.silent_manager.g29.silent_manager.data_layer.repository

import android.util.Log
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import com.silent_manager.g29.silent_manager.data_layer.models.Response
import com.silent_manager.g29.silent_manager.data_layer.request.ErrorInterpreter
import com.silent_manager.g29.silent_manager.data_layer.request.IRequest
import com.silent_manager.g29.silent_manager.data_layer.request.JsonConverter
import org.json.JSONObject

open class Repository(
    override val request: IRequest,
    val HOST_API: String
) : IRepository {
    companion object {
        const val TAG: String = "Repository"
    }

    protected fun <D : Any> entityToJSON(data: D): JSONObject {
        val eventStringify: String = JsonConverter.convertToJson(data)
        return JSONObject(eventStringify)
    }

    protected inline fun <reified T : Any> dispatchJsonMessage(it: Response<String>): Response<T> {
        var result: T? = null

        try {
            it.result?.let { it1 -> result = JsonConverter.convert(it1) }
        } catch (e: Exception) {
            Log.d(TAG, e.message)
            Response(
                result,
                RequestError(
                    ErrorInterpreter.ErrorCode.UNEXPECTED_RESPONSE_FORMAT,
                    e.message.toString()
                )
            )
        }

        return Response(result, it.error)
    }

    protected fun parseParametersToUrl(parameterTokens: List<Pair<String, String>>): String {
        return parameterTokens
            .mapIndexed { index, it ->
                "${it.first}=${it.second}${if (index + 1 < parameterTokens.size) "&" else ""}"
            }
            .joinToString(prefix = "", postfix = "", separator = "")
            .replace(" ", "+")
    }
}