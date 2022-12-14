package com.rndeep.fns_fantoo.ui.chatting.chatlist

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatListResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//@HiltViewModel
class ChatListViewModel : ViewModel() {
    private val _chatList = makeTmpChatList().toMutableStateList()
    val chatList: List<ChatListResult> get() = _chatList

    private val _optionOpenedChatId = MutableStateFlow<Long?>(null)
    val optionOpenedChatId: StateFlow<Long?> get() = _optionOpenedChatId

    private fun makeTmpChatList(): List<ChatListResult> = mutableListOf<ChatListResult>().apply {
        (0L..30L).forEach {
            add(ChatListResult(chatId = it, count = it))
        }
    }

    fun exitChat(chatId: Long) {
        closeOptions(chatId)
        _chatList.removeIf { it.chatId == chatId }
    }

    fun openOptions(chatId: Long) {
        _optionOpenedChatId.value = chatId
    }

    fun closeOptions(cardId: Long) {
        if (_optionOpenedChatId.value != cardId) {
            return
        }
        _optionOpenedChatId.value = null
    }

//
//    private fun onClickChat(chatId: Long) {
//        viewModelScope.launch {
//            _navigateToChat.emit(chatId)
//        }
//    }
}