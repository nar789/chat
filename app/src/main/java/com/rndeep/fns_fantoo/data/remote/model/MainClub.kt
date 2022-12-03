package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class MainClub (
    @field:SerializedName("clubManageId") val clubManageId: String,
    @field:SerializedName("profileImgUrl") val profileImgUrl: String,
    @field:SerializedName("backgroundImgUrl") val backgroundImgUrl: String,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("memberNum") val memberNum: String,
    @field:SerializedName("isOwner") val isOwner: Boolean,
    @field:SerializedName("isView") val isView: Boolean,
    @field:SerializedName("bookmark") val bookmark: Boolean,
)