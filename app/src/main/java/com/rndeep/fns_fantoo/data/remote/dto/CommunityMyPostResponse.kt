package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityMyPost

data class CommunityMyPostResponse(
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int,
    @field:SerializedName("post") val postList: List<CommunityMyPost>,
)
