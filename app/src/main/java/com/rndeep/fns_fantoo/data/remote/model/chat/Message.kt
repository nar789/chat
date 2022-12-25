package com.rndeep.fns_fantoo.data.remote.model.chat

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Message(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("conversation_id") val conversationId: Int? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("message_type") val messageType: Int? = null,
    @SerializedName("message") val message: String = "",
    @SerializedName("updated") val updated: Long? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("name") val name: String? = null
) {
    val dateText = updated?.let { dateFormat.format(it * 1000).toString() }.orEmpty()
    val hourText = updated?.let { hourFormat.format(it * 1000).toString() }.orEmpty()
        .replace("AM", "오전")
        .replace("PM", "오후")

    fun isMyMessage(myId: String) = userId == myId
    fun getUnReadCount(readInfos: List<ReadInfo>): Int {
        return readInfos.filter { (it.lastMessageId ?: 0) <= id }.size
    }
}

val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.ROOT)
val hourFormat = SimpleDateFormat("a hh:mm", Locale.ROOT)