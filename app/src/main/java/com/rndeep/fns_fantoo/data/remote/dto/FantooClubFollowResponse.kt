package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FantooClubFollowResponse(
    @SerializedName("followCount") val followCount: Int,
    @SerializedName("followYn") val followYn: Boolean
)

data class FantooClubIsFollowResponse(
    @SerializedName("followYn") var followYn: Boolean
)