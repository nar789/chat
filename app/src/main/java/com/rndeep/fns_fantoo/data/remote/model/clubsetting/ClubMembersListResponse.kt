package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoDto

data class ClubMembersListResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("msg") val msg: String,
    @field:SerializedName("dataObj") val dataObj: ClubMembersObj,
)

data class ClubMembersListDto(
    @field:SerializedName("memberList") val memberList: List<ClubMemberInfoDto>,
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int?,
    @field:SerializedName("memberCount") val memberCount: Int,
    @field:SerializedName("totalSearchCnt") val totalSearchCnt: Int
)
