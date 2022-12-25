package com.rndeep.fns_fantoo.repositories

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatRoomModel
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatListResult
import com.rndeep.fns_fantoo.data.remote.model.chat.CreateChatBody
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


class ChatRepository @Inject constructor(private val socketManager: ChatSocketManager) {

    companion object {
        private const val RESULT_SUCCESS = "success"
        private const val RESULT_FAIL = "fail"

        private const val KEY_RESULT = "result"
        private const val KEY_DATA = "data"

        private const val PARAM_USER_ID = "userId"
        private const val PARAM_INFO = "info"
    }

    private val _createConversationResult = mutableStateOf(false)
    val createConversationResult: State<Boolean> get() = _createConversationResult

    private val _chatList = mutableStateListOf<ChatRoomModel>()
    val chatList: List<ChatRoomModel> get() = _chatList

    init {
        listenAll()
    }

    private fun listenAll() {
        listenLoadConversation()
    }

    //todo
    private fun listenCreateConversation() {
//        socketManager.on(ChatSocketEvent.CREATE_CONVERSATION) {
//            _createConversationResult.value = true
////                it.get()
//        }
    }

    private fun listenLoadConversation() {
        socketManager.on(ChatSocketEvent.LOAD_CONVERSATION) { response ->
            if (response?.get(KEY_RESULT)?.isSuccess() != true) {
                return@on
            }

            val data: String = response[KEY_DATA]?: return@on
            _chatList.addAll(data.toObjectList())
        }
    }

    fun requestChatList(userId: String) {
        _chatList.clear()
        socketManager.emit(ChatSocketEvent.LOAD_CONVERSATION, mapOf(PARAM_USER_ID to userId))
    }

    fun requestCreateChat(userId: String) {
        val test1 = CreateChatBody(
            id = userId,
            name = "hi",
            profile = "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fi.pinimg.com%2Foriginals%2Fba%2F72%2Fe6%2Fba72e63ac96ccaeb4f128701f9d4cd7d.jpg&type=sc960_832"
        )
        val test2 = CreateChatBody(
            id = "1",
            name = "hi",
            profile = "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fi.pinimg.com%2Foriginals%2Fba%2F72%2Fe6%2Fba72e63ac96ccaeb4f128701f9d4cd7d.jpg&type=sc960_832"
        )
        val test3 = CreateChatBody(
            id = "2",
            name = "hi",
            profile = "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fi.pinimg.com%2Foriginals%2Fba%2F72%2Fe6%2Fba72e63ac96ccaeb4f128701f9d4cd7d.jpg&type=sc960_832"
        )
        val body = Gson().toJson(listOf(test1, test2, test3)).toString()

        socketManager.emit(ChatSocketEvent.CREATE_CONVERSATION, mapOf(PARAM_INFO to body))
    }

    fun startSocket() {
        socketManager.connectSocket()
    }

    fun finish() {
        socketManager.finish()
    }

    private fun String.isSuccess() = this == RESULT_SUCCESS

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