package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost

data class FantooClubPostsResponse (
    @SerializedName("listSize") val listSize : Int,
    @SerializedName("nextId") val nextId : Int?,
    @SerializedName("postList") val postList : List<FantooClubPost>
)