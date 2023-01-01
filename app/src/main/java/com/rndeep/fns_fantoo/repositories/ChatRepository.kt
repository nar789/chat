package com.rndeep.fns_fantoo.repositories

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.animation.core.snap
import androidx.compose.runtime.mutableStateListOf
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatRoomInfo
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatUserInfo
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.model.chat.ReadInfo
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import com.rndeep.fns_fantoo.ui.chatting.chat.MessageDataSource
import com.rndeep.fns_fantoo.utils.convertFileToByteArray
import com.rndeep.fns_fantoo.utils.isSuccess
import com.rndeep.fns_fantoo.utils.toObjectList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val socketManager: ChatSocketManager,
    private val contentResolver: ContentResolver
) {

    companion object {
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
        private const val PARAM_FILE = "file"
    }

    private val _createConversationResult = MutableSharedFlow<Int>()
    val createConversationResult: SharedFlow<Int> get() = _createConversationResult

    private val _chatList = mutableStateListOf<ChatRoomInfo>()
    val chatList: List<ChatRoomInfo> get() = _chatList

    private val _messagesListFlow = MutableSharedFlow<List<Message>>()
    val messagesListFlow: SharedFlow<List<Message>> get() = _messagesListFlow

    private val _messagesFlow = MutableSharedFlow<Message>()
    val messagesFlow: SharedFlow<Message> get() = _messagesFlow

    private val _readInfoFlow = MutableSharedFlow<ReadInfo>()
    val readInfoFlow: SharedFlow<ReadInfo?> get() = _readInfoFlow

    private val _uploadImageFlow = MutableSharedFlow<String>()
    val uploadImageFlow: SharedFlow<String> get() = _uploadImageFlow

    private val _showErrorToast = MutableSharedFlow<String>()
    val showErrorToast: SharedFlow<String> get() = _showErrorToast

    init {
        listenAll()
    }

    fun startSocket() {
        socketManager.connectSocket()
    }

    fun finish() {
        socketManager.finish()
    }

    private fun listenAll() {
        listenCreateConversation()
        listenLoadConversation()
        listenLoadMessage()
        listenMessage()
        listenReadInfo()
        listenUploadImage()
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
            val chatId: Int? = response?.get(PARAM_CONVERSATION_ID)?.toIntOrNull()
            if (!response?.get(KEY_RESULT).isSuccess() || chatId == null || chatId < 0) {
                showErrorToast("채팅방을 생성할 수 없습니다.")
                return@on
            }
            notifyCreateConversation(chatId)
        }
    }

    private fun showErrorToast(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _showErrorToast.emit(message)
        }
    }

    private fun notifyCreateConversation(conversationId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _createConversationResult.emit(conversationId)
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
            CoroutineScope(Dispatchers.IO).launch {
                _messagesListFlow.emit(messageList)
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
                _messagesFlow.emit(message)
            }
        }
    }

    private fun listenUploadImage() {
        socketManager.on(ChatSocketEvent.UPLOAD_IMAGE) { response ->
            val fileName = response?.get("filename") ?: return@on
            CoroutineScope(Dispatchers.IO).launch {
                _uploadImageFlow.emit(fileName)
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
                "name" to message.displayName,
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

    fun uploadImage(uri: Uri) {
        val fileName = uri.lastPathSegment
        val data = uri.convertFileToByteArray(contentResolver)
        if (data == null) {
            showErrorToast("업로드 할 수 없는 이미지 입니다.")
            return
        }
        socketManager.emit(
            ChatSocketEvent.UPLOAD_IMAGE,
            mapOf(PARAM_FILE to data, PARAM_NAME to fileName)
        )
    }

    private var messageDataStore: MessageDataSource? = null
    private val snapshots = mutableListOf<Message>()
    private var useSnapshot = false
    fun getMessageList(
        conversationId: Int
    ): Flow<PagingData<Message>> = Pager(PagingConfig(pageSize = 10)) {
        MessageDataSource(socketManager, conversationId, messagesListFlow, snapshots, useSnapshot).also {
            messageDataStore = it
        }
    }.flow

    fun invalidateMessageList(message: Message) {
        snapshots.add(message)
        useSnapshot = true
        messageDataStore?.invalidate()
    }
}