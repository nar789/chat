package com.rndeep.fns_fantoo.data.remote.model.fantooclub

import com.google.gson.annotations.SerializedName

data class ReportMessage(
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("reportMessageId") val reportMessageId: Int,
)