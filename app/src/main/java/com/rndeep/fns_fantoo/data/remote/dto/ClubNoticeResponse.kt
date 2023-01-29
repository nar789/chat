package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.club.ClubNoticeItem

data class ClubNoticeResponse (
    @SerializedName("postList") val postList : List<ClubNoticeItem>,
    @SerializedName("listSize") val listSize : Int,
    @SerializedName("nextId") val nextId : Int,

)