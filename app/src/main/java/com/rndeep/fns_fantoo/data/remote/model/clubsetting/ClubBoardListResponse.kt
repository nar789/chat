package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName

data class ClubBoardListResponse (
    @field:SerializedName("code") val code: String,
    @field:SerializedName("msg") val msg: String,
    @field:SerializedName("dataObj") val dataObj: List<ClubBoard>
)