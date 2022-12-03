package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.RecommendationClub

//추천 클럽 리스트
data class RecommendClubData(
    @SerializedName("code")
    val code: String,
    @SerializedName("dataObj")
    val recommendList: List<RecommendationClub>,
    @SerializedName("msg")
    val msg: String
)
