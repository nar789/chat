package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CheckPasswordResponse(
    @field:SerializedName("isMatchePw") val isMatchePw: Boolean
)