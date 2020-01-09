package com.silent_manager.Request

import com.silent_manager.g29.silent_manager.data_layer.models.Response
import com.silent_manager.g29.silent_manager.data_layer.repository.EventRepository
import com.silent_manager.g29.silent_manager.data_layer.repository.Repository
import com.silent_manager.g29.silent_manager.data_layer.repository.UserRepository
import com.silent_manager.g29.silent_manager.data_layer.request.IRequest
import org.json.JSONObject
import java.io.*


class FileRequest : IRequest {
    override fun post(url: String, data: JSONObject, callback: (Response<String>) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val endpoints: HashMap<String, String> = HashMap()

    constructor() {
        val classLoader: ClassLoader = ClassLoader.getSystemClassLoader()

        val singleUserFilePath = classLoader.getResource("Single_User_MockUp.json").toURI()
        val usersByParamsFilePath = classLoader.getResource("Get_User_By_Params_MockUp.json").toURI()
        val eventsByParamsFilePath = classLoader.getResource("Get_Events_MockUp.json").toURI()

        endpoints["${Repository.HOST_API}${UserRepository.USER_END_POINT}/1"] =
                File(singleUserFilePath).readText()
        endpoints["${Repository.HOST_API}${UserRepository.USER_END_POINT}?username=guil&username=g"] =
                File(usersByParamsFilePath).readText()
        endpoints["${Repository.HOST_API}${EventRepository.EVENT_END_POINT}?latitude=1&longitude=1&radius=100"] =
                File(eventsByParamsFilePath).readText()
    }

    override fun get(url: String, callback: (Response<String>) -> Unit) {
        callback(Response(endpoints[url]!!, null))
    }

}