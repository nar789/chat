package com.rndeep.fns_fantoo.data.remote.model.userinfo

import com.google.gson.annotations.SerializedName

data class PasswordValidator(
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("loginPw") val password: String
)
