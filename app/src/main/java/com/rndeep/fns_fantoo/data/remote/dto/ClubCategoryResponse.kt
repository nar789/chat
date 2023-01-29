package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItem

data class ClubCategoryResponse (
    @SerializedName("categoryList")val categoryList :List<ClubCategoryItem>,
    @SerializedName("listSize") val listSize :Int
)