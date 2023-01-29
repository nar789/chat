package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.userinfo.WalletHistory


data class MyWalletHistoryResponse(
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int,
    @field:SerializedName("walletList") val walletList: List<WalletHistory>,
)
