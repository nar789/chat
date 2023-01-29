package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData

data class ClubPagePostResponse (
    @SerializedName("postList") val postList : List<ClubPostData>,
    @SerializedName("listSize") val listSize : Int,
    @SerializedName("nextId") val nextId : Int?,
)