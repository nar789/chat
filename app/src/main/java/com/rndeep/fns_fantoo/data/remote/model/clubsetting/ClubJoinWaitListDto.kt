package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoDto

data class ClubJoinWaitListDto(
    @field:SerializedName("joinList") val joinList: List<ClubJoinWaitMemberDto>,
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int?,
)

data class ClubJoinWaitMemberDto(
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("joinId") val joinId: String,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("profileImg") val profileImg: String,
)

data class ClubJoinWaitMemberWithMeta(
    val clubJoinWaitMember : ClubJoinWaitMemberDto,
    val listSize : Int
)