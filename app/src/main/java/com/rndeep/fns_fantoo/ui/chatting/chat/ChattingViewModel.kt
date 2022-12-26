package com.rndeep.fns_fantoo.ui.chatting.chat

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.repositories.*
import com.rndeep.fns_fantoo.ui.chatting.profiledetail.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private var otherUserId: String = "testId"
    private lateinit var accessToken: String
    private lateinit var myUserInfo: UserInfoResponse
    private lateinit var myName: String
    private lateinit var myPhoto: String

    init {
        Timber.d("init")
        viewModelScope.launch {
            dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?.let { myUid ->
                _chatUiState.value = _chatUiState.value.copy(myId = myUid)
            }

            accessToken =
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()

            fetchUserInfo()
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            chatRepository.requestLeave(chatId)
        }
    }

    fun init(chatId: Int) {
        Log.d("sujini", "init: $chatId")
        this.chatId = chatId
        checkChatBlockedState()
        initMessageState()
    }

    private fun initMessageState() {
        viewModelScope.launch {
            chatRepository.requestJoin(chatId)
            chatRepository.requestLoadMessage(chatId, 0, 100)
            _chatUiState.value = _chatUiState.value.copy(
                messages = chatRepository.messageList
            )
        }
    }

    fun initProfileDetail(userId: String) {
        Timber.d("initProfileDetail $userId")
        viewModelScope.launch {
            otherUserId = userId

            val myId = _chatUiState.value.myId
            val blocked = chatUserRepository.isUserBlocked(myId, userId)
            val followed = chatUserRepository.isUserFollowed(myId, userId)
            _profileUiState.value =
                _profileUiState.value.copy(blocked = blocked, followed = followed)
        }
    }

    fun sendMessage(message: String) {
        // TODO : upload message to server

        // TODO : temp code remove this
        viewModelScope.launch {
            chatRepository.sendMessage(
                Message(
                    userId = _chatUiState.value.myId,
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
            chatUserRepository.setUserBlocked(_chatUiState.value.myId, otherUserId, blocked)
        }
    }

    fun setConversationUnBlock() {
        viewModelScope.launch {
            chatUserRepository.setConversationBlocked(_chatUiState.value.myId, chatId, true)
        }
    }

    fun followUser(follow: Boolean) {
        viewModelScope.launch {
            _profileUiState.value = _profileUiState.value.copy(followed = follow)
            chatUserRepository.setUserFollowed(_chatUiState.value.myId, otherUserId, follow)
        }
    }

    private fun checkChatBlockedState() {
        viewModelScope.launch {
            val convBlocked =
                chatUserRepository.isConversationBlocked(_chatUiState.value.myId, chatId)

            // TODO: check user blocked state
            val anyUserBlocked = false

            _chatUiState.value = _chatUiState.value.copy(blocked = convBlocked || anyUserBlocked)
        }
    }

    private fun fetchUserInfo() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(_chatUiState.value.myId))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                myUserInfo = response.data
                myName = myUserInfo.userNick.orEmpty()
                myPhoto = myUserInfo.userPhoto.orEmpty()
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                Timber.d("network error")
            }
        }
    }
}