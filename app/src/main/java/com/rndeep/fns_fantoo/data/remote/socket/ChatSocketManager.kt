package com.rndeep.fns_fantoo.data.remote.socket

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    // 방법 1
    private val createConversationListeners = mutableListOf<((Boolean) -> Unit)>()

    // 방법 2
    private val _createConversationResult = mutableStateOf(false)
    val createConversationResult: State<Boolean> get() = _createConversationResult

    fun init() {
        connectSocket()
        listenForTest()
        listenSocketError()
        listenCreateConversation()
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

    private fun listenForTest() {
        socket.on("welcome") {
            Timber.d("receive connected message welcome!")
        }
    }

    // 방법1
    fun addCreateConversationListener(listener: (Boolean) -> Unit) {
        createConversationListeners.add(listener)
    }

    private fun listenCreateConversation() {
        on(ChatSocketEvent.CREATE_CONVERSATION) {
            val result = (it.firstOrNull() as? Boolean) ?: false
            // 방법 1
            createConversationListeners.forEach { listener -> listener(result) }
            // 방법 2
            _createConversationResult.value = result
        }
    }

    private fun on(event: String, onSuccess: (Array<Any>) -> Unit) {
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