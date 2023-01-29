package com.rndeep.fns_fantoo.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData
import kotlinx.parcelize.Parcelize

data class ClubCategoryItem(
    @SerializedName("clubId") val clubId: String,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("boardType") val boardType: Int,
    @SerializedName("url") val url: String,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("depth") val depth: Int,
    @SerializedName("sort") val sort: Int,
    @SerializedName("openYn") val openYn: Boolean,
    @SerializedName("categoryType") val categoryType: Int,
    @SerializedName("showYn") val showYn: Boolean,
    @SerializedName("commonYn") val commonYn: Boolean?,
    @SerializedName("categoryList") val categoryList: List<ClubSubCategoryItem>,
) {
    fun toClubCategoryItemPacerable(): ClubCategoryItemPacerable {
        return ClubCategoryItemPacerable(
            clubId,
            categoryId,
            categoryCode,
            boardType,
            url,
            categoryName,
            depth,
            sort,
            openYn,
            categoryType,
            showYn,
            commonYn,
            categoryList.map { it.toClubSubCategoryItemPacerable() }
        )
    }
}

@Parcelize
data class ClubCategoryItemPacerable(
    val clubId: String,
    val categoryId: String,
    val categoryCode: String,
    val boardType: Int,
    val url: String,
    val categoryName: String,
    val depth: Int,
    val sort: Int,
    val openYn: Boolean,
    val categoryType: Int,
    val showYn: Boolean,
    val commonYn: Boolean?,
    val categoryList: List<ClubSubCategoryItemPacerable>
) : Parcelable

data class ClubCategoryItemResponse(
    val code: String,
    val msg:String?,
    val data: ClubCategoryItem?,
    val errorData: ErrorData?

)