package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem

data class ChallengePostResponse(
    @SerializedName("postList") val postList: List<ClubChallengeItem>,
    @SerializedName("listSize") val listSize : Int,
    @SerializedName("nextId") val nextId : Int,
)
