package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class MainClubListResponse (
    @field:SerializedName("code") val code: String,
    @field:SerializedName("msg") val msg: String,
    @field:SerializedName("dataObj") val dataObj: List<MainClub>
)
