package com.rndeep.fns_fantoo.ui.chatting.chat

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _chatUiState = mutableStateOf(ChatUiState())
    val chatUiState: State<ChatUiState> get() = _chatUiState

    init {
        Timber.d("init")
        viewModelScope.launch {
            val myUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            _chatUiState.value = _chatUiState.value.copy(myId = myUid)
        }

        // get userBlocked State
    }

    fun initProfileDetail(userId: String) {
        // sync userBlockedState, userFollowedState
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
        _chatUiState.value = _chatUiState.value.copy(userBlocked = blocked)
        // TODO: user block server call
    }

    fun followUser(follow: Boolean) {
        _chatUiState.value = _chatUiState.value.copy(userFollowed = follow)
        // follow server call
    }
}