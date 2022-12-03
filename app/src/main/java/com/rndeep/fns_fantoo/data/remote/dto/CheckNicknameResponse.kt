package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CheckNicknameResponse(
    @field:SerializedName("isCheck") val isCheck: Boolean
)