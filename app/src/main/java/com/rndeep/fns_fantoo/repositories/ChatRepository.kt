package com.rndeep.fns_fantoo.repositories

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import javax.inject.Inject

class ChatRepository @Inject constructor(private val socketManager: ChatSocketManager) {

    private val _createConversationResult = mutableStateOf(false)
    val createConversationResult: State<Boolean> get() = _createConversationResult

    private val _chatList = mutableStateOf(arrayOf<Any>())
    val chatList: State<Array<Any>> get() = _chatList

    init {
        listenAll()
    }

    private fun listenAll() {
        listenLoadConversation()
    }

    private fun listenCreateConversation() {
        socketManager.on(ChatSocketEvent.CREATE_CONVERSATION) {
            val result = (it.firstOrNull() as? Boolean)?: false
            _createConversationResult.value = result
        }
    }

    private fun listenLoadConversation() {
        socketManager.on(ChatSocketEvent.LOAD_CONVERSATION) {
            //todo
            _chatList.value = it
        }
    }

    fun startSocket() {
        socketManager.connectSocket()
    }

    fun finish() {
        socketManager.finish()
    }
}