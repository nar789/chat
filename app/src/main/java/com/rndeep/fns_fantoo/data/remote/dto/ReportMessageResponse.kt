package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.ReportMessageItem

data class ReportMessageResponse(
    @SerializedName("reportList") val reportList :List<ReportMessageItem>,
    @SerializedName("listSize") val listSize :Int,
)
