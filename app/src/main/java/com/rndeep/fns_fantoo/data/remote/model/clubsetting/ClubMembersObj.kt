package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.ClubMemberInfo

data class ClubMembersObj(
    @field:SerializedName("data") val data: List<ClubMemberInfo>,
    @field:SerializedName("totalCount") val totalCount: Int,
)

