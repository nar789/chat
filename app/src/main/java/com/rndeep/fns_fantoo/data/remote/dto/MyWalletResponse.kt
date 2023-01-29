package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName


data class MyWalletResponse(
    @field:SerializedName("fanit") val fanit: Int,
    @field:SerializedName("honor") val honor: Int,
    @field:SerializedName("kdg") val kdg: Int,
)