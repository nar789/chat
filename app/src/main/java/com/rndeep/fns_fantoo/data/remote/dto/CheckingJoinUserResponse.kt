package com.rndeep.fns_fantoo.data.remote.dto

import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.dto.CheckingJoinUserDto

data class CheckingJoinUserResponse(
    val code:String,
    val msg:String?,
    val checkingJoinUserDto: CheckingJoinUserDto?,
    val errorData: ErrorData?
)
