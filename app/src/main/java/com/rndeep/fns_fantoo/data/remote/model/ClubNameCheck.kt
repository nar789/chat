package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class ClubNameCheck(
    @field:SerializedName("existYn") val existYn: Boolean,
    @field:SerializedName("checkToken") val checkToken: String
)
