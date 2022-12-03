package com.rndeep.fns_fantoo.data.remote

data class ErrorBody(
    val code: String?,
    val msg: String?,
    val dataObj: ErrorData?
)

data class ErrorData(
    val path: String,
    val time: String,
    val error: String,
    val message: String,
)