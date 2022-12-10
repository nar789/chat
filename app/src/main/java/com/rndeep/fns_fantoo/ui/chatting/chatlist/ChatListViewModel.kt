package com.rndeep.fns_fantoo.ui.chatting.chatlist

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatListResult

//@HiltViewModel
class ChatListViewModel : ViewModel() {
    private val _chatList = makeTmpChatList().toMutableStateList()
    val chatList: List<ChatListResult> get() = _chatList

    private fun makeTmpChatList(): List<ChatListResult> = mutableListOf<ChatListResult>().apply {
        (0L..30L).forEach {
            add(ChatListResult(chatId = it, count = it))
        }
    }
}