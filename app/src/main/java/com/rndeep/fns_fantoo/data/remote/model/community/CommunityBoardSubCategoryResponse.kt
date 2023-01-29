package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName

data class CommunityBoardSubCategoryResponse (
    @SerializedName("code")var code : String,
    @SerializedName("msg")var msg : String,
    @SerializedName("dataObj")var dataObj : BoardSubCategory
)

data class BoardSubCategory(
    @SerializedName("category") var categoryData : CategoryBoardCategoryList,
    @SerializedName("categoryList") var subCategoryList : List<CategoryBoardCategoryList>,
    @SerializedName("listSize") var categorySize :Int
)
