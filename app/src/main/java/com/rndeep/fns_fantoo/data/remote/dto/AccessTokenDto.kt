package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AccessTokenDto(
    @field:SerializedName("access_token") val accessToken: String,
    @field:SerializedName("refresh_token") val refreshToken: String,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("token_type") val tokenType: String,
    @field:SerializedName("expires_in") val expiresIn: String,
)
