package com.rndeep.fns_fantoo.data.remote.model.chat

import com.google.gson.annotations.SerializedName

data class TargetIntegUid(
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("targetIntegUid") val targetIntegUid: String
)
