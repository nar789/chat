package com.rndeep.fns_fantoo.repositories

import android.util.Log
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import javax.inject.Inject

class ChatRepository @Inject constructor(private val socketManager: ChatSocketManager) {

    fun startSocket() {
        socketManager.init()
    }

    fun finish() {
        socketManager.finish()
    }

    fun requestChatList() {
        //todo coroutine?
        socketManager.on(ChatSocketEvent.LOAD_CONVERSATION) {
            Log.d("inwha", "data: ${it.firstOrNull()}, ${it.getOrNull(1)}")
        }
    }
}