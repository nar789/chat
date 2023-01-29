package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName

data class LikeDislikeResponse(
    @SerializedName("code") val code : String,
    @SerializedName("msg") val msg : String,
    @SerializedName("dataObj") val dataObj : LikeDislikeResult
)

data class LikeDislikeResult(
    @SerializedName("like") val like : Int,
    @SerializedName("disLike") val dislike : Int
)
