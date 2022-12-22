package com.rndeep.fns_fantoo.data.remote.model.chat

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("id") val id: Int,
    @SerializedName("conversation_id") val conversationId: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("message_type") val messageType: Int? = null,
    @SerializedName("message") val message: String,
    @SerializedName("updated") val updated: Long? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("name") val name: String? = null
)
