package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatConversationDto(
    @field:SerializedName("conversationId") val conversationId: String,
    @field:SerializedName("integUid") val integUid: String
)