package com.rndeep.fns_fantoo.data.remote.model.club

import com.google.gson.annotations.SerializedName

data class ClubInterestItem (
    @SerializedName("clubInterestCategoryId") val clubInterestCategoryId : Int,
    @SerializedName("categoryCode") val categoryCode : String,
    @SerializedName("langCode") val langCode : String,
    @SerializedName("codeName") val codeName : String,
    @SerializedName("categoryName") val categoryName : String,
)