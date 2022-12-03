package com.rndeep.fns_fantoo.data.remote.model.fantooclub

import com.google.gson.annotations.SerializedName

data class FantooClubCategory(
    @SerializedName("boardType") val boardType: Int,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("categoryType") val categoryType: Int,
    @SerializedName("clubId") val clubId: String,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("commonYn") val commonYn: Boolean,
    @SerializedName("depth") val depth: Int,
    @SerializedName("firstImageList") val firstImageList: List<String>?,
    @SerializedName("openYn") val openYn: Boolean,
    @SerializedName("parentCategoryId") val parentCategoryId: Int,
    @SerializedName("postCount") val postCount: Int,
    @SerializedName("sort") val sort: Int,
    @SerializedName("url") val url: String,
)
