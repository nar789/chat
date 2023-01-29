package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName

data class CommunityNoticeDetailResponse (
    @SerializedName("notice")val notice : CommunityNoticeData,
    @SerializedName("attachList")val attachList : List<DetailAttachList>?,
)

data class CommunityNoticeData(
    @SerializedName("postId")val postId : Int,
    @SerializedName("title")var title : String,
    @SerializedName("content")var content : String,
    @SerializedName("topYn")val topYn : Boolean,
    @SerializedName("userNick")val userNick : String,
    @SerializedName("userPhoto")val userPhoto : String,
    @SerializedName("createDate")val createDate : String,
    @SerializedName("translateYn")var translateYn : Boolean?,

)

