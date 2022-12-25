package com.rndeep.fns_fantoo.data.remote.socket

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import timber.log.Timber
import java.net.URISyntaxException
import javax.inject.Inject


class ChatSocketManager @Inject constructor() {
    companion object {
        private const val SERVER_URL = "http://nar005.cafe24.com:1225"
    }

    private lateinit var socket: Socket

    init {
        try {
            socket = IO.socket(SERVER_URL)
        } catch (e: URISyntaxException) {
            Timber.e("socket init error: ${e.reason}", e)
        }

        listenForTest()
        listenSocketError()
    }

    fun connectSocket() {
        Timber.d("try to connecting socket")

        socket.connect()
        socket.on(Socket.EVENT_CONNECT) {
            //todo 연결 완료 시 서버에 보낼 정보 있는지 확인 필요
            Timber.d("Socket is connected")
        }
    }

    private fun listenForTest() {
        socket.on("welcome") {
            Timber.d("receive connected message welcome!")
        }
    }

    fun on(event: String, onResponse: (Map<String, String>?) -> Unit) {
        socket.on(event) {
            val response = it.getResponse()
            Timber.d("socket on --> event: $event, response: $response")
            onResponse(response)
        }
    }

    fun on(event: String, onResponse: () -> Unit) {
        socket.on(event) {
            Timber.d("socket on --> evnet: $event")
            onResponse()
        }
    }

    fun emit(event: String, args: Map<String, String> = emptyMap()) {
        if (args.isEmpty()) {
            socket.emit(event)
            return
        }
        val parms = JSONObject()
        args.forEach{
            parms.put(it.key, it.value)
        }
        socket.emit(event, parms)
    }

    private fun listenSocketError() {
        socket.on(Socket.EVENT_DISCONNECT) {
            closeSocket(Socket.EVENT_DISCONNECT + ": ${it.firstOrNull()}")
        }.on(Socket.EVENT_CONNECT_ERROR) {
            closeSocket(Socket.EVENT_CONNECT_ERROR + ": ${it.firstOrNull()}")
        }
    }

    fun finish() {
        closeSocket(reason = "ChatSocketManager is finished")
    }

    private fun closeSocket(reason: String = "") {
        Timber.w("session is disconnected: $reason")
        socket.close()
    }

    private fun Array<Any>.getResponse(): Map<String, String>? = (firstOrNull() as? JSONObject)?.toMap()

    private fun JSONObject.toMap(): Map<String, String> = mutableMapOf<String, String>().apply {
        keys().forEach {
            this[it] = this@toMap[it].toString()
        }
    }
//    private fun <T> String.toObject(): T? {
//        return Gson().fromJson(this, object : TypeToken<T>() {}.type)
//    }

//    private inline fun <reified T> T.toJson(): String = Gson().toJson(this)
//
//    private inline fun <reified T, K, V> Map<K, V>.toObject(): T = Gson().fromJson(this.toJson(), T::class.java)
}