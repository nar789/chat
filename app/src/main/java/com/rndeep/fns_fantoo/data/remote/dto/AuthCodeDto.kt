package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthCodeDto(
    @field:SerializedName("state") val state: String,
    @field:SerializedName("authCode") val authCode: String
)