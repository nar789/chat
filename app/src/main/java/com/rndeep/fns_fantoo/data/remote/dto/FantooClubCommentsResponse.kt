package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubComment

data class FantooClubCommentsResponse(
    @SerializedName("listSize") val listSize: Int,
    @SerializedName("nextId") val nextId: Int?,
    @SerializedName("replyList") val replyList: List<FantooClubComment>
)
