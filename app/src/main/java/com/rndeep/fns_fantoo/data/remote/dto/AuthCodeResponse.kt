package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class AuthCodeResponse (
    val code:String?,
    val msg:String?,
    val authCodeDto: AuthCodeDto?,
    val errorData: ErrorData?
)