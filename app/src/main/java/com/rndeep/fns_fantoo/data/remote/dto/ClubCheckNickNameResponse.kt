package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class ClubCheckNickNameResponse(
    val code: String,
    val msg: String,
    val dataObj: ClubCheckNickNameDto?,
    val errorData: ErrorData?
)

data class ClubCheckNickNameDto(
    @field:SerializedName("existYn") val existYn: Boolean,
    @field:SerializedName("checkToken") val checkToken: String
)