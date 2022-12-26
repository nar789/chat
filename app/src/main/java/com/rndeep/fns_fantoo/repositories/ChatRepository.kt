package com.rndeep.fns_fantoo.repositories

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatRoomModel
import com.rndeep.fns_fantoo.data.remote.model.chat.CreateChatUserInfo
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import timber.log.Timber
import javax.inject.Inject


class ChatRepository @Inject constructor(private val socketManager: ChatSocketManager) {

    companion object {
        private const val RESULT_SUCCESS = "success"
        private const val RESULT_FAIL = "fail"

        private const val KEY_RESULT = "result"
        private const val KEY_DATA = "data"
        private const val KEY_ROWS = "rows"

        private const val PARAM_USER_ID = "userId"
        private const val PARAM_INFO = "info"
        private const val PARAM_NAME = "name"
        private const val PARAM_CONVERSATION_ID = "conversationId"
        private const val PARAM_OFFSET = "offset"
        private const val PARAM_SIZE = "size"
        private const val PARAM_ROOM = "room"
    }

    private val _createConversationResult = mutableStateOf(false)
    val createConversationResult: State<Boolean> get() = _createConversationResult

    private val _chatList = mutableStateListOf<ChatRoomModel>()
    val chatList: List<ChatRoomModel> get() = _chatList

    private val _messageList = mutableStateListOf<Message>()
    val messageList: List<Message> get() = _messageList

    init {
        listenAll()
    }

    private fun listenAll() {
        listenCreateConversation()
        listenLoadConversation()
        listenLoadMessage()
        listenMessage()
    }

    private fun listenCreateConversation() {
        socketManager.on(ChatSocketEvent.CREATE_CONVERSATION) { response ->
            _createConversationResult.value = response?.get(KEY_RESULT).isSuccess()
        }
    }

    private fun listenLoadConversation() {
        socketManager.on(ChatSocketEvent.LOAD_CONVERSATION) { response ->
            if (response?.get(KEY_RESULT)?.isSuccess() != true) {
                return@on
            }

            val data: String = response[KEY_DATA] ?: return@on
            _chatList.clear()
            _chatList.addAll(data.toObjectList())
        }
    }

    private fun listenLoadMessage() {
        socketManager.on(ChatSocketEvent.LOAD_MESSAGE) { response ->
            val rows: String = response?.get(KEY_ROWS) ?: return@on
            val messageList: List<Message> = rows.toObjectList<Message>().reversed()
            _messageList.addAll(messageList)
        }
    }

    private fun listenMessage() {
        socketManager.on(ChatSocketEvent.MESSAGE) { response ->
            if (response == null) return@on
            val message = Message(
                id = response["messageId"]?.toInt() ?: 0,
                conversationId = response["conversationId"]?.toInt(),
                userId = response["userId"],
                messageType = response["messageType"]?.toInt() ?: 1,
                message = response["message"].orEmpty(),
                updated = response["updated"]?.toLong() ?: 0L,
                image = response["image"],
                name = response["name"]
            )
            _messageList.add(message)
        }
    }

    fun requestChatList(userId: String) {
        Timber.d("requestChatList: $userId")
        socketManager.emit(ChatSocketEvent.LOAD_CONVERSATION, mapOf(PARAM_USER_ID to userId))
    }

    fun requestCreateChat(users: List<CreateChatUserInfo>) {
//        val test1 = CreateChatUserInfo(
//            id = userId,
//            name = "hi",
//            profile = "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fi.pinimg.com%2Foriginals%2Fba%2F72%2Fe6%2Fba72e63ac96ccaeb4f128701f9d4cd7d.jpg&type=sc960_832"
//        )
//        val test2 = CreateChatUserInfo(
//            id = "1",
//            name = "hi",
//            profile = "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fi.pinimg.com%2Foriginals%2Fba%2F72%2Fe6%2Fba72e63ac96ccaeb4f128701f9d4cd7d.jpg&type=sc960_832"
//        )
//        val test3 = CreateChatUserInfo(
//            id = "2",
//            name = "hi",
//            profile = "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fi.pinimg.com%2Foriginals%2Fba%2F72%2Fe6%2Fba72e63ac96ccaeb4f128701f9d4cd7d.jpg&type=sc960_832"
//        )
        val body = Gson().toJson(users).toString()
        socketManager.emit(ChatSocketEvent.CREATE_CONVERSATION, mapOf(PARAM_INFO to body))
    }

    fun requestExitChatRoom(userId: String, name: String, conversationId: Int) {
        socketManager.emit(
            ChatSocketEvent.OUT_CONVERSATION,
            mapOf(
                PARAM_USER_ID to userId,
                PARAM_NAME to name,
                PARAM_CONVERSATION_ID to conversationId.toString()
            )
        )
    }

    fun requestLoadMessage(conversationId: Int, offset: Int, size: Int) {
        _messageList.clear()
        socketManager.emit(
            ChatSocketEvent.LOAD_MESSAGE,
            mapOf(
                PARAM_CONVERSATION_ID to conversationId.toString(),
                PARAM_OFFSET to offset.toString(),
                PARAM_SIZE to size.toString()
            )
        )
    }

    fun requestLeave(conversationId: Int) {
        socketManager.emit(
            ChatSocketEvent.LEAVE,
            mapOf(
                PARAM_ROOM to conversationId.toString()
            )
        )
    }

    fun requestJoin(conversationId: Int) {
        socketManager.emit(
            ChatSocketEvent.JOIN,
            mapOf(
                PARAM_ROOM to conversationId.toString()
            )
        )
    }

    fun sendMessage(message: Message) {
        socketManager.emit(
            ChatSocketEvent.MESSAGE,
            mapOf(
                "conversationId" to message.conversationId.toString(),
                "userId" to message.userId,
                "name" to message.name,
                "message" to message.message,
                "image" to message.image,
                "messageType" to message.messageType.toString(),
                "updated" to (message.updated?.div(1000L)).toString()
            )
        )
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
                add(mapToObject(it, T::class.java) ?: return@forEach)
            }
        }
    }

    private fun <T> mapToObject(map: Map<String, Any?>?, type: Class<T>): T? {
        if (map == null) return null

        val gson = Gson()
        val json = gson.toJson(map)
        return gson.fromJson(json, type)
    }

    //convert a data class to a map
    fun <T> T.serializeToMap(): Map<String, String> {
        return convert()
    }

    //convert a map to a data class
    inline fun <reified T> Map<String, Any>.toDataClass(): T {
        return convert()
    }

    //convert an object of type I to type O
    inline fun <I, reified O> I.convert(): O {
        val gson = Gson()
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }


}