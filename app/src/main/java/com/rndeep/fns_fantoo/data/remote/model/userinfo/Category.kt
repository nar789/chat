package com.rndeep.fns_fantoo.data.remote.model.userinfo

import com.google.gson.annotations.SerializedName

data class Category(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("codeName") val codeName: String,
    @field:SerializedName("description") var description: String,
    @field:SerializedName("langCode") val langCode: String,
    @field:SerializedName("selectYn") var selectYn: Boolean,
    @field:SerializedName("sort") val sort: Int,
)