package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class VerifyCodeCheckResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("isCheck") val isCheck: Boolean
)
