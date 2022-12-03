package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class ClubNickNameDuplicateDataObj(
    @SerializedName("existYn") val isCheck :Boolean,
    @SerializedName("checkToken") val checkToken :String
)