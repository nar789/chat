package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClubWithDrawMemberListDto(
    @field:SerializedName("withdrawList") val withdrawList: List<ClubWithDrawMemberInfoDto>,
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int?,
    @field:SerializedName("memberCount") val memberCount: Int,
    @field:SerializedName("totalMemberCount") val totalMemberCount: Int
)

data class ClubWithDrawMemberInfoDto(
    @field:SerializedName("clubId") val clubId: Int,
    @field:SerializedName("clubWithdrawId") val clubWithdrawId: Int,
    @field:SerializedName("createDate") val createDate: String?,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("joinYn") val joinYn: Boolean,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("profileImg") val profileImg: String,
    @field:SerializedName("updateDate") val updateDate: String,
)

data class ClubWithDrawMemberInfoWithMeta(
    val clubWithDrawMemberInfo : ClubWithDrawMemberInfoDto,
    val totalMemberCount : Int,
    val listSize : Int
)