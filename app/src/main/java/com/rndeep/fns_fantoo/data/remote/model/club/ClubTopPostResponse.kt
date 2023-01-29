package com.rndeep.fns_fantoo.data.remote.model.club

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData

data class ClubTopPostResponse(
    @SerializedName("listSize") val listSize :Int,
    @SerializedName("postList") val postList :List<ClubPostData>,
    @SerializedName("date") val date :String?,
)
