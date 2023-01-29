package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class RecommendationClub(
    @field:SerializedName("clubId") val clubManageId: Int,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("memberCountOpenYn") val memberCountOpenYn: Boolean,
    @field:SerializedName("openYn") val openYn: Boolean,
    @field:SerializedName("profileImg") val profileImgUrl: String,
    @field:SerializedName("bgImg") val bgImg: String?,
    @field:SerializedName("memberCount") val memberCount: Int,
    @field:SerializedName("joinStatus") val joinStatus: Int,
    @field:SerializedName("hashtagList") val hashtagList: List<String>?,
    @field:SerializedName("status") val status: Int
)