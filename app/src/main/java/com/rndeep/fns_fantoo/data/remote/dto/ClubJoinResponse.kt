package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClubJoinResponse(
    @SerializedName("memberJoinAutoYn") val memberJoinAutoYn : Boolean,
)
