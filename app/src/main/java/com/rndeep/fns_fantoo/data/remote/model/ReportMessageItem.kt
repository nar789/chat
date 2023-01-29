package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class ReportMessageItem (
    @SerializedName("reportMessageId") val reportMessageId :Int,
    @SerializedName("message") val message : String,
)