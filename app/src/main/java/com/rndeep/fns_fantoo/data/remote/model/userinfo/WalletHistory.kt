package com.rndeep.fns_fantoo.data.remote.model.userinfo

import com.google.gson.annotations.SerializedName

data class WalletHistory(
    @field:SerializedName("comment") val comment: String,
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("historyId") val historyId: Int,
    @field:SerializedName("monthAndDate") val monthAndDate: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("value") val value: Int
)
