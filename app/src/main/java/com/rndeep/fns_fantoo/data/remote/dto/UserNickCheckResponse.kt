package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class UserNickCheckResponse(
    val code: String,
    val msg:String?,
    val userNickCheckDto: UserNickCheckDto?,
    val errorData: ErrorData?
)
