package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatUserInfoResponse(
    @field:SerializedName("blockYn") var blockYn: Boolean,
    @field:SerializedName("followYn") var followYn: Boolean,
    @field:SerializedName("userNick") val userNick: String,
    @field:SerializedName("userPhoto") val userPhoto: String?
)
