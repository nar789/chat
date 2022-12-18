package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.runtime.Immutable
import java.text.SimpleDateFormat
import java.util.*

data class ChatUiState(
    val messages: List<Message> = listOf(),
    val myId: Long = 0,
    val translateMode: Boolean = false,
    val userBlocked: Boolean = false,
    val userFollowed: Boolean = false
)

val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.ROOT)
val hourFormat = SimpleDateFormat("a hh:mm", Locale.ROOT)

@Immutable
data class Message(
    val id: Long = 0,
    val content: String = "",
    val authorId: Long = 0,
    val authorName: String = "",
    val authorImage: String? = null,
    private val timestamp: Long = System.currentTimeMillis(),
    val image: String? = null,
    val unreadCount: Int = 0
) {
    val dateText = dateFormat.format(timestamp).toString()
    val hourText = hourFormat.format(timestamp).toString()
        .replace("AM", "오전")
        .replace("PM", "오후")

    fun isMyMessage(myId: Long) = authorId == myId
}