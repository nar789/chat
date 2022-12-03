package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class RegistResponse(
    val code: String,
    val msg: String?,
    val registResult: RegistResult?,
    val errorData: ErrorData?
)
