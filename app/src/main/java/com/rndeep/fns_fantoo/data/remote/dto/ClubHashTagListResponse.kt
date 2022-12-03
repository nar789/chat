package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class ClubHashTagListResponse(
    val code: String,
    val msg: String,
    val dataObj: ClubHashTagListDto?,
    val errorData: ErrorData?
)

data class ClubHashTagListDto(
    @field:SerializedName("clubId") val clubId: String,
    @field:SerializedName("hashtagList") val hashtagList: List<String>,
    @field:SerializedName("listSize") val listSize: Int,
)