package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class ClubRecommendCategory(
    @SerializedName("categoryCode")val categoryCode : String,
    @SerializedName("categoryCodeName")val categoryCodeName : String,
)
