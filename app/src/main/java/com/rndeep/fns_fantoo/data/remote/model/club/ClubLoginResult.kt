package com.rndeep.fns_fantoo.data.remote.model.club

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class ClubLoginResult(
    @SerializedName("memberId") val memberId : Int,
    @SerializedName("joinStatus") val joinStatus : Int,
    @SerializedName("delegateStatus") val delegateStatus : Int,
)

data class ClubLoginResponse(
    val code : String,
    val message : String?,
    val data : ClubLoginResult?,
    val errorData : ErrorData?
)