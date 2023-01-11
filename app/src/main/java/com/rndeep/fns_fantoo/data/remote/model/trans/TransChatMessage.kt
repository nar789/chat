package com.rndeep.fns_fantoo.data.remote.model.trans

import com.google.gson.annotations.SerializedName

data class TransChatMessage(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("user") val user: String?,
    @SerializedName("isTranslated") val isTranslated: Boolean,
)
