package com.rndeep.fns_fantoo.utils

import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber
import java.net.URISyntaxException
import javax.inject.Inject

class ChatSocketManager @Inject constructor() {
    companion object {
        private val TAG = "ChatSocketManager"
    }

    private lateinit var socket: Socket

    fun init() {
        connectSocket()
        listenSocketError()
    }

    private fun connectSocket() {
        try {
            socket = IO.socket("") // todo url 필요
        } catch (e: URISyntaxException) {
            Timber.e(TAG, "socket init error: ${e.reason}", e)
        }

        socket.connect()
        socket.on(Socket.EVENT_CONNECT) {
            //todo 연결 완료 시 서버에 보낼 정보 있는지 확인 필요
            Timber.d(TAG, "Socket is connected")
        }
    }

    //todo listenr외부에서 받을건지, state문 주입받을건지 논의 필요
    fun listenChatEvent() {
        socket.on("chat") {

        }
    }

    fun listenChatListEvent() {
        socket.on("chatList") {

        }
    }

    private fun listenSocketError() {
        socket.on(Socket.EVENT_DISCONNECT) {
            closeSocket(Socket.EVENT_DISCONNECT)
        }.on(Socket.EVENT_CONNECT_ERROR) {
            closeSocket(Socket.EVENT_CONNECT_ERROR)
        }.on(Socket.EVENT_CONNECT_TIMEOUT) {
            closeSocket(Socket.EVENT_CONNECT_TIMEOUT)
        }.on(Socket.EVENT_ERROR) {
            closeSocket(Socket.EVENT_ERROR)
        }
    }

    fun finish() {
        closeSocket(reason = "ChatSocketManager is finished")
    }

    private fun closeSocket(reason: String = "") {
        Timber.i(TAG, "session io is disconnected: $reason")
        socket.close()
    }
}