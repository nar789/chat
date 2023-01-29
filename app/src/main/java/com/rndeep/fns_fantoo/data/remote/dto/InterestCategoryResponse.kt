package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.userinfo.Category

data class InterestCategoryResponse(
    @field:SerializedName("interestList") val interestList: List<Category>,
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int,
)

