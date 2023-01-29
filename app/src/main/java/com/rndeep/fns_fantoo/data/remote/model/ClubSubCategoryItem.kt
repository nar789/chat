package com.rndeep.fns_fantoo.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ClubSubCategoryItem(
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
    @SerializedName("parentCategoryId") val parentCategoryId: String,
    @SerializedName("postCount") val postCount: Int,
    @SerializedName("sort") val sort: Int,
    @SerializedName("url") val url: String,
) {
    fun toClubSubCategoryItemPacerable(): ClubSubCategoryItemPacerable {
        return ClubSubCategoryItemPacerable(
            boardType,
            categoryCode,
            categoryId,
            categoryType,
            clubId,
            categoryName,
            commonYn,
            depth,
            firstImageList,
            openYn,
            parentCategoryId,
            postCount,
            sort,
            url
        )
    }
}

@Parcelize
data class ClubSubCategoryItemPacerable(
    val boardType: Int,
    val categoryCode: String,
    val categoryId: String,
    val categoryType: Int,
    val clubId: String,
    val categoryName: String?,
    val commonYn: Boolean,
    val depth: Int,
    val firstImageList: List<String>?,
    val openYn: Boolean,
    val parentCategoryId: String,
    val postCount: Int,
    val sort: Int,
    val url: String
) : Parcelable