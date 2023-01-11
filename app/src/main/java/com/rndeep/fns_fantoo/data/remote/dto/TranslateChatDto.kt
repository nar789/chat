package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.trans.TransChatMessage

data class TranslateChatDto(
    @SerializedName("status") val status: String,
    @SerializedName("language") val language: List<String>,
    @SerializedName("messages") val messages: List<TransChatMessage>,
)