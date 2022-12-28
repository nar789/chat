package com.rndeep.fns_fantoo.ui.chatting.chat

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ChatUserInfoResponse
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.model.chat.ReadInfo
import com.rndeep.fns_fantoo.repositories.*
import com.rndeep.fns_fantoo.ui.chatting.profiledetail.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val chatUserRepository: ChatUserRepository,
    private val userInfoRepository: UserInfoRepository,
) : ViewModel() {

    private val _chatUiState = mutableStateOf(ChatUiState())
    val chatUiState: State<ChatUiState> get() = _chatUiState

    private val _profileUiState = mutableStateOf(ProfileUiState())
    val profileUiState: State<ProfileUiState> get() = _profileUiState

    private var chatId: Int = 0
    private var targetUserId: String = ""
    private var readInfoMap: MutableMap<String, ReadInfo> = mutableMapOf()

    private lateinit var myUid: String
    private lateinit var accessToken: String
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
    }

    private fun initMessageState() {
        viewModelScope.launch {
            chatRepository.requestLeave(chatId)
            chatRepository.requestJoin(chatId)
            chatRepository.requestLoadMessage(chatId, 0, 100)
            chatRepository.requestReadInfo(chatId, myUid)
            chatRepository.requestLoadReadInfo(chatId)
            _chatUiState.value = _chatUiState.value.copy(
                messages = chatRepository.messageList
            )

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
    }

    fun initProfileDetail(userId: String) {
        Timber.d("initProfileDetail $userId")
        targetUserId = userId

        viewModelScope.launch {
            fetchChatUserInfo(userId)?.let { chatUserInfo ->
                _profileUiState.value = _profileUiState.value.copy(
                    blocked = chatUserInfo.blockYn,
                    followed = chatUserInfo.followYn,
                    name = chatUserInfo.userNick,
                    photo = chatUserInfo.userPhoto
                )
            }
        }
    }

    fun sendMessage(message: String) {
        // TODO : upload message to server

        // TODO : temp code remove this
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

    fun sendImageMessage(images: List<Uri>) {
        // TODO : send images to server
    }

    fun setTranslateMode(onOff: Boolean) {
        Log.d("sujini", "setTranslateMode: $onOff")
        _chatUiState.value = _chatUiState.value.copy(translateMode = onOff)
    }

    fun setUserBlock(blocked: Boolean) {
        viewModelScope.launch {
            _profileUiState.value = _profileUiState.value.copy(blocked = blocked)
            chatUserRepository.setUserBlock(accessToken, myUid, targetUserId, blocked)
        }
    }

    fun setConversationUnBlock() {
        viewModelScope.launch {
            chatUserRepository.setConversationBlocked(myUid, chatId, true)
        }
    }

    fun followUser(follow: Boolean) {
        viewModelScope.launch {
            _profileUiState.value = _profileUiState.value.copy(followed = follow)
            chatUserRepository.setUserFollow(accessToken, myUid, targetUserId, follow)
        }
    }

    private fun checkChatBlockedState() {
        viewModelScope.launch {
            val convBlocked =
                chatUserRepository.isConversationBlocked(myUid, chatId)

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

    private suspend fun fetchChatUserInfo(userId: String): ChatUserInfoResponse? {
        val response = chatUserRepository.fetchChatUserInfo(accessToken, myUid, userId)
        Timber.d("fetchChatUserInfo : $response")
        return when (response) {
            is ResultWrapper.Success -> response.data
            else -> null
        }
    }
}