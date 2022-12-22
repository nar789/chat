package com.rndeep.fns_fantoo.data.remote.model.chat

import com.google.gson.annotations.SerializedName

data class Conversation(
    @SerializedName("id") val id: Int,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("last_message_id") val lastMessageId: Int? = null,
    @SerializedName("user_count") val userCount: Int? = null
)
