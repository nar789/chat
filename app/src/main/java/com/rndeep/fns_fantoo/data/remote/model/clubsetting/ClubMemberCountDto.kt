package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName

data class ClubMemberCountDto(
    @field:SerializedName("memberCount") val memberCount: Int
)
