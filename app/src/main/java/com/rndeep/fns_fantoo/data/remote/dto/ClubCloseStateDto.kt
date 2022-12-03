package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClubCloseStateDto(
    @SerializedName("closesStatus") val closesStatus :Int,
    @SerializedName("clubId") val clubId :Int
)
