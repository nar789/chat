package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.runtime.Immutable

class ChatUiState {
}

@Immutable
data class Message(
    val content: String,
    val authorName: String
)