package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @field:SerializedName("access_data") val accessData: String,
    @field:SerializedName("expires_in") val expiresIn: String,
)