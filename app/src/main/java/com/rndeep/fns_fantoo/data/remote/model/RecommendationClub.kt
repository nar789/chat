package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class RecommendationClub(
    @field:SerializedName("clubId") val clubManageId: String,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("openYn") val openYn: Boolean,
    @field:SerializedName("clubSearchWord") val clubSearchWord: List<String>?,
    @field:SerializedName("profileImg") val profileImgUrl: String,
    @field:SerializedName("status") val status: Int
)