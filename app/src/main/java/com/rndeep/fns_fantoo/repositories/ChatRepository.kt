package com.rndeep.fns_fantoo.repositories

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatRoomModel
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatUserInfo
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.model.chat.ReadInfo
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
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
        private const val PARAM_LAST_MESSAGE_ID = "lastMessageId"
    }

    private var createConversationCallback: ((Boolean, Int) -> Unit)? = null

    private val _chatList = mutableStateListOf<ChatRoomModel>()
    val chatList: List<ChatRoomModel> get() = _chatList

    private val _messagesFlow = MutableSharedFlow<List<Message>>()
    val messagesFlow: SharedFlow<List<Message>> get() = _messagesFlow

    private val _readInfoFlow = MutableSharedFlow<ReadInfo>()
    val readInfoFlow: SharedFlow<ReadInfo?> get() = _readInfoFlow

    init {
        listenAll()
    }

    private fun listenAll() {
        listenCreateConversation()
        listenLoadConversation()
        listenLoadMessage()
        listenMessage()
        listenReadInfo()
    }

    private fun listenReadInfo() {
        socketManager.on(ChatSocketEvent.READ_INFO) { response ->
            if (response == null) return@on
            val readInfo = ReadInfo(
                conversationId = response["conversationId"]?.toInt(),
                userId = response["userId"],
                lastMessageId = response["lastMessageId"]?.toInt()
            )

            CoroutineScope(Dispatchers.IO).launch {
                _readInfoFlow.emit(readInfo)
            }
        }
    }

    private fun listenCreateConversation() {
        socketManager.on(ChatSocketEvent.CREATE_CONVERSATION) { response ->
            if (!response?.get(KEY_RESULT).isSuccess()) {
                notifyCreateConversation(false, -1)
                return@on
            }
            notifyCreateConversation(
                true,
                (response?.get("conversationId")?.toIntOrNull() ?: return@on)
            )
        }
    }

    private fun notifyCreateConversation(success: Boolean, conversationId: Int) {
        createConversationCallback?.invoke(success, conversationId)
    }

    fun setCreateConversationCallback(callback: ((success: Boolean, conversationId: Int) -> Unit)) {
        createConversationCallback = callback
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
            CoroutineScope(Dispatchers.IO).launch {
                _messagesFlow.emit(messageList)
            }
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
            CoroutineScope(Dispatchers.IO).launch {
                _messagesFlow.emit(listOf(message))
            }
        }
    }

    fun requestChatList(userId: String) {
        Timber.d("requestChatList: $userId")
        socketManager.emit(ChatSocketEvent.LOAD_CONVERSATION, mapOf(PARAM_USER_ID to userId))
    }

    fun requestTmpCreateChat(users: List<ChatUserInfo>) {
        val body = Gson().toJson(users).toString()
        socketManager.emit(ChatSocketEvent.CREATE_CONVERSATION, mapOf(PARAM_INFO to body))
    }

    fun requestCreateChat(users: List<GetUserListResponse.ChatUserDto>) {
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

    fun requestLoadReadInfo(conversationId: Int) {
        socketManager.emit(
            ChatSocketEvent.LOAD_READ_INFO,
            mapOf(PARAM_CONVERSATION_ID to conversationId.toString())
        )
    }

    fun requestReadInfo(conversationId: Int, userId: String) {
        socketManager.emit(
            ChatSocketEvent.READ_INFO,
            mapOf(
                PARAM_CONVERSATION_ID to conversationId.toString(),
                PARAM_USER_ID to userId,
            )
        )
    }

    fun startSocket() {
        socketManager.connectSocket()
    }

    fun finish() {
        createConversationCallback = null
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
    private fun <T> T.serializeToMap(): Map<String, String> {
        return convert()
    }

    //convert a map to a data class
    private inline fun <reified T> Map<String, Any>.toDataClass(): T {
        return convert()
    }

    //convert an object of type I to type O
    private inline fun <I, reified O> I.convert(): O {
        val gson = Gson()
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }
}