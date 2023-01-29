package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClubStorageCountDto(
    @SerializedName("postCount") val postCount : Int,
    @SerializedName("replyCount") val replyCount : Int,
    @SerializedName("bookmarkCount") val bookmarkCount : Int
)
