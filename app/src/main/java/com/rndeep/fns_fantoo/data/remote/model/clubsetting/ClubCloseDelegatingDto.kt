package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName

data class ClubCloseDelegatingDto(
    @field:SerializedName("body") val body: List<String>,
    @field:SerializedName("statusCode") val statusCode: String,
    @field:SerializedName("statusCodeValue") val statusCodeValue: Int,
)
