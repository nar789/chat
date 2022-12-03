package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class ClubMemberInfo(
    @field:SerializedName("clubMemberManageId") val clubMemberManageId: String,
    @field:SerializedName("memberNickname") val memberNickname: String,
    @field:SerializedName("memberProfileUrl") val memberProfileUrl: String,
    @field:SerializedName("memberType") val memberType: String,
    @field:SerializedName("joinDate") val joinDate: String,
    @field:SerializedName("loginId") val loginId: String
)
