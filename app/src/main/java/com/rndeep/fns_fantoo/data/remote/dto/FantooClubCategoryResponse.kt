package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubCategory

data class FantooClubCategoryResponse(
    @SerializedName("boardType") val boardType: Int,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("categoryList") val categoryList: List<FantooClubCategory>,
    @SerializedName("categoryType") val categoryType: Int,
    @SerializedName("clubId") val clubId: String,
    @SerializedName("codeNameEn") val codeNameEn: String,
    @SerializedName("codeNameKo") val codeNameKo: String,
    @SerializedName("commonYn") val commonYn: Boolean,
    @SerializedName("depth") val depth: Int,
    @SerializedName("openYn") val openYn: Boolean,
    @SerializedName("showYn") val showYn: Boolean,
    @SerializedName("sort") val sort: Int,
    @SerializedName("url") val url: String
)
