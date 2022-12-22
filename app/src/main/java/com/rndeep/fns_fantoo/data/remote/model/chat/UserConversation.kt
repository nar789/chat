package com.rndeep.fns_fantoo.data.remote.model.chat

import com.google.gson.annotations.SerializedName

data class UserConversation(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("conversation_id") val conversationId: Int? = null,
    @SerializedName("is_block") val isBlock: Boolean? = null,
    @SerializedName("is_alarm") val isAlarm: Boolean? = null
)
