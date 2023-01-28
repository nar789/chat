package com.rndeep.fns_fantoo.repositories

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.google.gson.Gson
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.TranslateService
import com.rndeep.fns_fantoo.data.remote.dto.TranslateChatDto
import com.rndeep.fns_fantoo.data.remote.dto.TranslationRequest
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatRoomInfo
import com.rndeep.fns_fantoo.data.remote.model.chat.CreateChatUserInfo
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.model.chat.ReadInfo
import com.rndeep.fns_fantoo.data.remote.model.trans.TransChatMessage
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import com.rndeep.fns_fantoo.ui.chatting.chat.MessageDataSource
import com.rndeep.fns_fantoo.utils.convertFileToByteArray
import com.rndeep.fns_fantoo.utils.isSuccess
import com.rndeep.fns_fantoo.utils.toObjectList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val context: Context,
    private val socketManager: ChatSocketManager,
    private val contentResolver: ContentResolver,
    private val translateApi: TranslateService
) : BaseNetRepo() {

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

    private val _exitUserEvent = MutableSharedFlow<String>()
    val exitUserEvent: SharedFlow<String> get() = _exitUserEvent

    private val _loadMessagesFlow = MutableSharedFlow<List<Message>>()
    private val loadMessagesFlow: SharedFlow<List<Message>> get() = _loadMessagesFlow

    private val _messageFlow = MutableSharedFlow<Message>()
    val messageFlow: SharedFlow<Message> get() = _messageFlow

    private val _readInfoFlow = MutableSharedFlow<ReadInfo>()
    val readInfoFlow: SharedFlow<ReadInfo?> get() = _readInfoFlow

    private val _uploadImageFlow = MutableSharedFlow<String>()
    val uploadImageFlow: SharedFlow<String> get() = _uploadImageFlow

    private val _showErrorToast = MutableSharedFlow<String>()
    val showErrorToast: SharedFlow<String> get() = _showErrorToast

    private var translateModeEvent = MutableSharedFlow<Boolean>()

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
            _chatList.addAll(
                (0..150).map {
                    ChatRoomInfo(
                        id = it,
                        title = "title$it"
                    )
                }
            )
        }
    }

    private fun listenLoadMessage() {
        socketManager.on(ChatSocketEvent.LOAD_MESSAGE) { response ->
            val rows: String = response?.get(KEY_ROWS) ?: return@on
            val messageList: List<Message> = rows.toObjectList()
            messageList.forEach(::convertType3Message)
            CoroutineScope(Dispatchers.IO).launch {
                _loadMessagesFlow.emit(messageList)
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
            ).also(::convertType3Message)

            CoroutineScope(Dispatchers.IO).launch {
                _messageFlow.emit(message)
            }
        }
    }

    private fun convertType3Message(message: Message) {
        if (message.messageType == 3) {
            message.message = context.getString(R.string.chatting_exit_message, message.displayName)
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

    fun requestCreateChat(users: List<CreateChatUserInfo>) {
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

    fun getMessageFlow(
        conversationId: Int,
        userId: String,
        accessToken: String
    ): Flow<PagingData<Message>> = Pager(
        config = PagingConfig(pageSize = 30),
        pagingSourceFactory = MessageDataSourceFactory(conversationId, userId, accessToken)
    ).flow

    fun setTranslateMode(translateMode: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            translateModeEvent.emit(translateMode)
        }
    }

    private suspend fun requestTranslate(
        accessToken: String,
        userId: String,
        messages: List<Message>
    ): ResultWrapper<TranslateChatDto> = safeApiCall(Dispatchers.IO) {
        val request = TranslationRequest(
            language = listOf("en"),
            messages = messages.map {
                TranslationRequest.MessageDto(
                    id = it.id.toString(),
                    text = it.message,
                    user = userId
                )
            }
        )
        translateApi.getTranslateWord(
            authToken = accessToken,
            deviceId = "",
            translationRequest = request
        )
    }.also { logCallResult("requestTranslate", it) }

    private fun <T> logCallResult(callName: String, result: ResultWrapper<T>) {
        when (result) {
            is ResultWrapper.Success -> Timber.d("$callName: ${result.data}")
            is ResultWrapper.GenericError -> Timber.e("$callName response error code : ${result.code} , server msg : ${result.message} , message : ${result.errorData?.message}")
            is ResultWrapper.NetworkError -> Timber.e("$callName NetworkError")
        }
    }

    private inner class MessageDataSourceFactory(
        private val conversationId: Int,
        private val userId: String,
        private val accessToken: String
    ) : () -> PagingSource<Int, Message> {
        private var cachedMessages = mutableListOf<Message>()
        private var dataSource: MessageDataSource? = null
        private var useCache: Boolean = false
        private var translateMode: Boolean = false

        private val messages
            get() = messageFlow
                .map(::mapToTranslatedMessage)
        private val loadMessages
            get() = loadMessagesFlow
                .map(::mapToTranslatedMessages)

        init {
            CoroutineScope(Dispatchers.Default).launch {
                // observe message change
                launch {
                    messages
                        .onEach {
                            if (it.messageType == 3) _exitUserEvent.emit(it.userId.orEmpty())
                            useCache = true
                            cachedMessages.add(0, it)
                            dataSource?.invalidate()
                        }
                        .collect()
                }

                // observe translateMode change
                launch {
                    translateModeEvent.onEach { mode ->
                        translateMode = mode

                        val otherUserMessages =
                            cachedMessages.filter { it.userId != userId && it.isNotTranslated }
                        otherUserMessages.chunked(30).forEach { messages ->
                            val translateMap = getTranslateMap(messages) ?: return@forEach
                            cachedMessages = cachedMessages.map { message ->
                                val key = message.id.toString()
                                if (translateMap.containsKey(key)) {
                                    message.copy(_translatedMessage = translateMap[key]?.text)
                                } else {
                                    message
                                }
                            }.toMutableList()

                            useCache = true
                            dataSource?.invalidate()
                        }
                    }.collect()
                }
            }
        }

        override fun invoke(): PagingSource<Int, Message> {
            return MessageDataSource(
                loadMessages,
                cachedMessages,
                useCache,
                requestLoadMessage = { offset, size ->
                    requestLoadMessage(conversationId, offset, size)
                },
                requestReadInfo = { requestReadInfo(conversationId, userId) }
            ).also {
                dataSource = it
                useCache = false
            }
        }

        private suspend fun mapToTranslatedMessages(messages: List<Message>): List<Message> {
            return if (translateMode) {
                val otherUserMessages = messages.filter { it.userId != userId }
                val translateMap = getTranslateMap(otherUserMessages) ?: return messages
                messages.map { message ->
                    val key = message.id.toString()
                    if (translateMap.containsKey(key)) {
                        message.copy(_translatedMessage = translateMap[key]?.text)
                    } else {
                        message
                    }
                }
            } else {
                messages
            }
        }

        private suspend fun mapToTranslatedMessage(message: Message): Message {
            return if (translateMode && message.userId != userId) {
                val translateMap = getTranslateMap(listOf(message)) ?: return message
                val key = message.id.toString()
                message.copy(_translatedMessage = translateMap[key]?.text)
            } else {
                message
            }
        }

        private suspend fun getTranslateMap(messages: List<Message>): Map<String, TransChatMessage>? {
            return requestTranslate(accessToken, userId, messages).let { response ->
                val result = when (response) {
                    is ResultWrapper.Success -> if (response.data.isSuccess) response.data else null
                    else -> null
                } ?: return@let null
                result.messages.associateBy { it.id }
            }
        }
    }
}