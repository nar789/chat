package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class ClubManageInfoResponse(
    val code: String,
    val msg: String,
    val dataObj:ClubManageInfoDto?,
    val errorData: ErrorData?
)

data class ClubManageInfoDto(
    @field:SerializedName("activeCountryCode") val activeCountryCode: String,
    @field:SerializedName("bgImg")  val bgImg: String,
    @field:SerializedName("clubId") val clubId: Int,
    @field:SerializedName("clubMasterNickname") val clubMasterNickname: String,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("hashtagList") val hashtagList: List<String>,
    @field:SerializedName("interestCategoryId") val interestCategoryId: Int,
    @field:SerializedName("introduction") val introduction: String,
    @field:SerializedName("joinMemberCount") val joinMemberCount: Int,
    @field:SerializedName("languageCode") val languageCode: String,
    @field:SerializedName("memberCount") val memberCount: Int,
    @field:SerializedName("memberCountOpenYn") val memberCountOpenYn: Boolean,
    @field:SerializedName("memberJoinAutoYn") val memberJoinAutoYn: Boolean,
    @field:SerializedName("memberListOpenYn") val memberListOpenYn: Boolean,
    @field:SerializedName("openYn") val openYn: Boolean,
    @field:SerializedName("postCount") val postCount: Int,
    @field:SerializedName("profileImg") val profileImg: String
)
