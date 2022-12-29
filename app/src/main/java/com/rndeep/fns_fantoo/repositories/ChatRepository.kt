package com.rndeep.fns_fantoo.repositories

import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatRoomModel
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatUserInfo
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class ChatRepository @Inject constructor(private val socketManager: ChatSocketManager) {

    companion object {
        private const val RESULT_SUCCESS = "success"
        private const val RESULT_FAIL = "fail"

        private const val KEY_RESULT = "result"
        private const val KEY_DATA = "data"

        private const val PARAM_USER_ID = "userId"
        private const val PARAM_INFO = "info"
        private const val PARAM_NAME = "name"
        private const val PARAM_CONVERSATION_ID = "conversationId"
    }

    private val _createConversationResult = SingleLiveEvent<Pair<Boolean, Int>>()
    val createConversationResult: LiveData<Pair<Boolean, Int>> get() = _createConversationResult

    private val _chatList = mutableStateListOf<ChatRoomModel>()
    val chatList: List<ChatRoomModel> get() = _chatList

    init {
        listenAll()
    }

    private fun listenAll() {
        listenCreateConversation()
        listenLoadConversation()
    }

    private fun listenCreateConversation() {
        socketManager.on(ChatSocketEvent.CREATE_CONVERSATION) { response ->
            CoroutineScope(Dispatchers.Main).launch {
                if (!response?.get(KEY_RESULT).isSuccess()) {
                    _createConversationResult.value = false to -1
                    return@launch
                }
                _createConversationResult.value =
                    true to (response?.get("conversationId")?.toIntOrNull() ?: return@launch)
            }
        }
    }

    private fun listenLoadConversation() {
        socketManager.on(ChatSocketEvent.LOAD_CONVERSATION) { response ->
            if (response?.get(KEY_RESULT)?.isSuccess() != true) {
                return@on
            }

            val data: String = response[KEY_DATA]?: return@on
            _chatList.clear()
            _chatList.addAll(data.toObjectList())
        }
    }

    fun requestChatList(userId: String) {
        socketManager.emit(ChatSocketEvent.LOAD_CONVERSATION, mapOf(PARAM_USER_ID to userId))
    }

    fun requestCreateChat(users: List<ChatUserInfo>) {
        val body = Gson().toJson(users).toString()
        socketManager.emit(ChatSocketEvent.CREATE_CONVERSATION, mapOf(PARAM_INFO to body))
    }

    fun requestExitChatRoom(userId: String, name: String, conversationId: Int) {
        socketManager.emit(ChatSocketEvent.OUT_CONVERSATION, mapOf(PARAM_USER_ID to userId, PARAM_NAME to name, PARAM_CONVERSATION_ID to conversationId.toString()))
    }

    fun startSocket() {
        socketManager.connectSocket()
    }

    fun finish() {
        socketManager.finish()
    }

    private fun String?.isSuccess(): Boolean = this == RESULT_SUCCESS

    private fun <T> String.toObject(): T = Gson().fromJson(this, object : TypeToken<T>() {}.type)

    private inline fun <reified T> String.toObjectList(): List<T> {
        val list: ArrayList<Map<String, Any?>>? = this.toObject()
        return mutableListOf<T>().apply {
            list?.forEach {
                add(mapToObject(it, T::class.java)?: return@forEach)
            }
        }
    }

    private fun <T> mapToObject(map: Map<String, Any?>?, type: Class<T>): T? {
        if (map == null) return null

        val gson = Gson()
        val json = gson.toJson(map)
        return gson.fromJson(json, type)
    }

}