package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName

data class CommunityBoardCategoryBody(
    @SerializedName("categoryList") val categoryBoardList: List<CategoryBoardCategoryList>,
    @SerializedName("listSize") val categorySize: Int
)

/**
 *  code : 카테고리 code
 *  codeNameEn : 카테고리 영문명
 *  codeNameKo : 카테고리 한글명
 *  favorite : 카테고리 즐겨찾기 여부
 *  integUid : 회원 U_id
 *  parentCode : 카테고리 code 2 depth
 *  depth : 카테고리 depth 단계
 *  sort : depth 별 정렬 순서
 */
data class CategoryBoardCategoryList(
    @SerializedName("code") val code: String,
    @SerializedName("codeNameEn") val codeNameEn: String,
    @SerializedName("codeNameKo") val codeNameKo: String,
    @SerializedName("favorite") val favorite: Boolean?,
    @SerializedName("integUid") val integUid: String,
    @SerializedName("parentCode") val parentCode: String,
    @SerializedName("depth") val depth: Int,
    @SerializedName("sort") val sort: Int,
)

data class CommunityNoticeName(
    val ko:String,
    val en:String
)



