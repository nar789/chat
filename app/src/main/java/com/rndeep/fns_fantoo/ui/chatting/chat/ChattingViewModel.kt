package com.rndeep.fns_fantoo.ui.chatting.chat

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.model.chat.ReadInfo
import com.rndeep.fns_fantoo.repositories.*
import com.rndeep.fns_fantoo.ui.chatting.chat.model.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val chatInfoRepository: ChatInfoRepository,
    private val userInfoRepository: UserInfoRepository,
) : ViewModel() {

    private val _chatUiState by lazy { mutableStateOf(ChatUiState()) }
    val chatUiState: State<ChatUiState> get() = _chatUiState

    private var chatId: Int = 0
    private var readInfoMap: MutableMap<String, ReadInfo> = mutableMapOf()
    private var userProfileMap: MutableMap<String, UserProfile> = mutableMapOf()

    lateinit var myUid: String
        private set
    lateinit var accessToken: String
        private set
    private lateinit var myName: String
    private lateinit var myPhoto: String

    init {
        viewModelScope.launch {
            myUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).orEmpty()
            accessToken =
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            _chatUiState.value = _chatUiState.value.copy(myId = myUid)

            val userInfo = fetchUserInfo()
            myName = userInfo?.userNick.orEmpty()
            myPhoto = userInfo?.userPhoto.orEmpty()
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            chatRepository.requestLeave(chatId)
        }
    }

    fun init(chatId: Int) {
        Timber.d("init(): $chatId")
        this.chatId = chatId
        checkChatBlockedState()
        initMessageState()
        initChatState()

        _chatUiState.value = _chatUiState.value.copy(
            messages = chatRepository.getMessageFlow(chatId, myUid)
                .map { pagingData ->
                    pagingData.map { findUserProfile(it) }
                }.cachedIn(viewModelScope)
        )
    }

    private fun initChatState() {
        viewModelScope.launch {
            chatRepository.requestChatList(myUid)
            delay(300)
            chatRepository.chatList
                .find { it.id == chatId }?.let { chatInfo ->
                    Log.d("sujini", "$chatInfo")
                    _chatUiState.value = _chatUiState.value.copy(
                        chatTitle = chatInfo.title,
                        userCount = chatInfo.userCount ?: 0
                    )
                }
        }
    }

    private fun initMessageState() {
        viewModelScope.launch {
            launch { collectReadInfoFlow() }
            launch { collectImageFlow() }
            launch { collectRefreshChatFlow() }

            chatRepository.requestLeave(chatId)
            chatRepository.requestJoin(chatId)
            chatRepository.requestLoadReadInfo(chatId)
            chatRepository.requestReadInfo(chatId, myUid)
        }
    }

    fun sendImageMessage(fileName: String, message: String = "") {
        viewModelScope.launch {
            chatRepository.sendMessage(
                Message(
                    userId = myUid,
                    name = myName,
                    image = fileName,
                    message = message,
                    conversationId = chatId,
                    messageType = 2,
                    updated = System.currentTimeMillis()
                )
            )
        }
    }

    fun sendTextMessage(message: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                Message(
                    userId = myUid,
                    name = myName,
                    message = message,
                    conversationId = chatId,
                    messageType = 1,
                    updated = System.currentTimeMillis()
                )
            )
        }
    }

    fun setTranslateMode(onOff: Boolean) {
        Log.d("sujini", "setTranslateMode: $onOff")
        _chatUiState.value = _chatUiState.value.copy(translateMode = onOff)
    }

    fun setConversationUnBlock() {
        viewModelScope.launch {
            chatInfoRepository.setConversationBlocked(accessToken, myUid, chatId, false)
                .also { response ->
                    val convBlocked = when (response) {
                        is ResultWrapper.Success -> response.data.blockYn
                        else -> false
                    }
                    _chatUiState.value = _chatUiState.value.copy(blocked = convBlocked)
                }
        }
    }

    private fun checkChatBlockedState() {
        viewModelScope.launch {
            val convBlocked = when (val response =
                chatInfoRepository.isConversationBlocked(accessToken, myUid, chatId)) {
                is ResultWrapper.Success -> response.data.blockYn
                else -> false
            }

            // TODO: check user blocked state
            val anyUserBlocked = false

            _chatUiState.value = _chatUiState.value.copy(blocked = convBlocked || anyUserBlocked)
        }
    }

    private suspend fun fetchUserInfo(): UserInfoResponse? {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(myUid))
        Timber.d("fetchUserInfo : $response")
        return when (response) {
            is ResultWrapper.Success -> response.data
            else -> null
        }
    }

    private suspend fun collectReadInfoFlow() {
        chatRepository.readInfoFlow
            .filterNotNull()
            .onEach {
                readInfoMap[it.userId.orEmpty()] = it
                _chatUiState.value = _chatUiState.value.copy(
                    readInfos = readInfoMap.values.toList()
                )
            }
            .collect()
    }

    private suspend fun collectImageFlow() {
        chatRepository.uploadImageFlow
            .filterNotNull()
            .onEach {
                sendImageMessage(it)
            }.collect()
    }

    private suspend fun collectRefreshChatFlow() {
        chatRepository.exitUserEvent
            .onEach {
                readInfoMap.remove(it)
                val oldUserCount = _chatUiState.value.userCount
                _chatUiState.value = _chatUiState.value.copy(userCount = oldUserCount - 1)
            }
            .collect()
    }

    private suspend fun findUserProfile(message: Message): Message {
        return message.apply {
            val userId = userId ?: return@apply
            if (userId == myUid) return@apply
            val userProfile = userProfileMap[userId] ?: fetchChatUserProfile(userId)
            userProfile?.let {
                displayName = it.name
                userPhoto = it.photo
            }
        }
    }

    private suspend fun fetchChatUserProfile(userId: String): UserProfile? {
        val response = chatInfoRepository.fetchChatUserInfo(accessToken, myUid, userId)
        Timber.d("fetchChatUserProfile : $response")
        return when (response) {
            is ResultWrapper.Success -> response.data.let { UserProfile(it.userNick, it.userPhoto) }
            else -> null
        }?.also { userProfileMap[userId] = it }
    }

    private data class UserProfile(
        val name: String?,
        val photo: String?
    )
}