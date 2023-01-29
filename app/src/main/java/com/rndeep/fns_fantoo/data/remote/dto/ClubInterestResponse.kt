package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.club.ClubInterestItem

data class ClubInterestResponse (
    @SerializedName("clubInterestCategoryDtoList")val clubInterestCategoryDtoList : List<ClubInterestCategoryDto>,
    @SerializedName("listSize")val listSize : Int,

)