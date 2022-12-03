package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class ClubMemberWithdrawDto(
    @field:SerializedName("joinYn") val joinYn: Boolean,
)

data class ClubMemberWithdrawResponse(
    val code:String,
    val message:String?,
    val data : ClubMemberWithdrawDto?,
    val errorData : ErrorData?
)
