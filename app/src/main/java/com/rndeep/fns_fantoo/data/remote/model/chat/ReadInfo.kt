package com.rndeep.fns_fantoo.data.remote.model.chat

import com.google.gson.annotations.SerializedName

data class ReadInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("conversation_id") val conversationId: Int? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("last_message_id") val lastMessageId: Int? = null
)
