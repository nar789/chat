package com.rndeep.fns_fantoo.data.remote.socket

import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber
import java.net.URISyntaxException
import javax.inject.Inject


class ChatSocketManager @Inject constructor() {
    companion object {
        private const val SERVER_URL = "http://nar005.cafe24.com:1225"
    }

    private lateinit var socket: Socket

    fun init() {
        connectSocket()
        listenForTest()
        listenSocketError()
    }

    private fun connectSocket() {
        Timber.d("try to connecting socket")
        try {
            socket = IO.socket(SERVER_URL)
        } catch (e: URISyntaxException) {
            Timber.e("socket init error: ${e.reason}", e)
        }
        socket.connect()
        socket.on(Socket.EVENT_CONNECT) {
            //todo 연결 완료 시 서버에 보낼 정보 있는지 확인 필요
            Timber.d("Socket is connected")
        }
    }

    fun listenForTest() {
        socket.on("welcome") {
            Timber.d("receive connected message welcome!")
        }
    }

    fun on(event: String, onSuccess: (Array<Any>) -> Unit) {
        socket.on(event) {
            Timber.d("on result: $it")
            onSuccess(it)
        }
    }

    fun emit(event: String, args: List<String>? = null) {
        socket.emit(event, args)
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
}