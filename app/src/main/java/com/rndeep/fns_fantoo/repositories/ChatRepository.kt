package com.rndeep.fns_fantoo.repositories

import androidx.compose.runtime.State
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import javax.inject.Inject

class ChatRepository @Inject constructor(private val socketManager: ChatSocketManager) {

    fun startSocket() {
        socketManager.init()
    }

    fun finish() {
        socketManager.finish()
    }

    // 방법 1
    fun listenCreateConversation(listener: (Boolean) -> Unit) {
        socketManager.addCreateConversationListener(listener)
    }

    // 방법 2
    fun getCreateConversationResult(): State<Boolean> = socketManager.createConversationResult
}