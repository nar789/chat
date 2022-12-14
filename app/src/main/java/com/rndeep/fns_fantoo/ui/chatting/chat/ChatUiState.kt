package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.runtime.Immutable

data class ChatUiState(
    val messages: List<Message>,
    val translateMode: Boolean = false
)

@Immutable
data class Message(
    val content: String = "",
    val authorName: String = "",
    val authorImage: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val image: String? = null,
    val unreadCount: Int = 0
) {
    val dateText = "오후 5:22"
}