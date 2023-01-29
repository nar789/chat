package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.ClubRecommendCategory

data class ClubRecommendCategoryResponse (
    @SerializedName("popularList")val popularList: List<ClubRecommendCategory>,
    @SerializedName("listSize")val listSize: Int,

)