package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class ClubInterestCategoryResponse(
    val code: String,
    val msg: String,
    val dataObj: ClubInterestCategoryDataDto?,
    val errorData: ErrorData?
)

data class ClubInterestCategoryDataDto(
    @field:SerializedName("clubInterestCategoryDtoList") val clubInterestCategoryDtoList: List<ClubInterestCategoryDto>,
    @field:SerializedName("listSize") val listSize: Int
)

data class ClubInterestCategoryDto(
    @field:SerializedName("clubInterestCategoryId") val clubInterestCategoryId: Int,
    @field:SerializedName("categoryName") val categoryName: String,
    @field:SerializedName("codeName") val codeName: String,
    @field:SerializedName("langCode") val langCode: String
)