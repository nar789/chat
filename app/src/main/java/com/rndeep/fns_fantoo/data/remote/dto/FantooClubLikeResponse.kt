package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FantooClubLikeResponse(
    @SerializedName("dislikeCount") val dislikeCount: Int,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("likeYn") val likeYn: Boolean?
)
