package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TranslationRequest(
    @field:SerializedName("language") val language: List<String>,
    @field:SerializedName("messages") val messages: List<MessageDto>
) {

    data class MessageDto(
        @field:SerializedName("id") val id: String,
        @field:SerializedName("text") val text: String,
        @field:SerializedName("user") val user: String
    )
}