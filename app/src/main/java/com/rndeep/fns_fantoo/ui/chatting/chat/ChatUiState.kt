package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.runtime.Immutable
import java.text.SimpleDateFormat
import java.util.*

data class ChatUiState(
    val messages: List<Message> = listOf(),
    val myId: String = "",
    val translateMode: Boolean = false,
    val userBlocked: Boolean = false,
    val userFollowed: Boolean = false,
    val readInfos: List<ReadInfo> = listOf()
)

val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.ROOT)
val hourFormat = SimpleDateFormat("a hh:mm", Locale.ROOT)

@Immutable
data class Message(
    val id: Long = 0,
    val content: String = "",
    val authorId: String = "",
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

    fun isMyMessage(myId: String) = authorId == myId
    fun getUnReadCount(readInfos: List<ReadInfo>): Int {
        return readInfos.filter { it.readTimeStamp <= timestamp }.size
    }
}

data class ReadInfo(
    val authorId: String = "",
    val readTimeStamp: Long = System.currentTimeMillis()
)