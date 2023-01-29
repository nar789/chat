package com.rndeep.fns_fantoo.data.remote.dto

import androidx.navigation.NavDirections
import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class ClubMemberBlockInfoResponse(
    val code: String,
    val msg: String,
    val dataObj: ClubMemberBlockInfoDto?,
    val navDirections: NavDirections?,
    val errorData: ErrorData?
)

data class ClubMemberBlockInfoDto(
    @field:SerializedName("blockYn") val blockYn: Boolean,
)
