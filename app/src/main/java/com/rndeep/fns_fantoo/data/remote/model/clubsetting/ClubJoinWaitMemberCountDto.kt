package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName

data class ClubJoinWaitMemberCountDto(
    @field:SerializedName("joinCount") val joinCount: Int
)
