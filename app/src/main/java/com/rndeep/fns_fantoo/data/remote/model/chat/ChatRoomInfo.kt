package com.rndeep.fns_fantoo.data.remote.model.chat

import com.google.gson.annotations.SerializedName

data class ChatRoomInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("last_message_id") val lastMessageId: Int? = null,
    @SerializedName("user_count") val userCount: Int? = null,
    @SerializedName("unreads") val unreads: Int? = 0,
    @SerializedName("userId") val userId: String? = "",
    @SerializedName("message") val message: String? = "",
    @SerializedName("message_type") val messageType: Int? = MESSAGE_TYPE_TEXT,
    @SerializedName("updated") val updated: Long? = 0
) {
    companion object {
        const val MESSAGE_TYPE_TEXT = 1
        const val MESSAGE_TYPE_IMAGE = 2
    }
}