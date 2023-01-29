package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityMyReply

data class CommunityMyReplyResponse(
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int,
    @field:SerializedName("reply") val replyList: List<CommunityMyReply>,
)
