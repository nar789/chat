package com.rndeep.fns_fantoo.ui.chatting.chat

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.ChatUserRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.chatting.profiledetail.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val chatUserRepository: ChatUserRepository
) : ViewModel() {

    private val _chatUiState = mutableStateOf(testUiState)
    val chatUiState: State<ChatUiState> get() = _chatUiState

    private val _profileUiState = mutableStateOf(ProfileUiState())
    val profileUiState: State<ProfileUiState> get() = _profileUiState

    private var chatId: Int = 0
    private var otherUserId: String = "testId"

    init {
        Timber.d("init")
        viewModelScope.launch {
            dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?.let { myUid ->
                _chatUiState.value = _chatUiState.value.copy(myId = myUid)
            }
        }
    }

    fun init(chatId: Int) {
        this.chatId = chatId
        checkChatBlockedState()
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
        val oldMessages = _chatUiState.value.messages
        _chatUiState.value = _chatUiState.value.copy(
            messages = oldMessages + listOf(
                Message(
                    authorId = _chatUiState.value.myId,
                    authorName = "me",
                    content = message
                )
            )
        )
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
}