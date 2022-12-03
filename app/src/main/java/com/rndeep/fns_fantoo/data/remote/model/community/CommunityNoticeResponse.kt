package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName

data class CommunityNoticeResponse(
    @SerializedName("code") var code: String,
    @SerializedName("msg") var mgs: String,
    @SerializedName("dataObj") var dataObj: CommunityNoticeDataObj,
)

data class CommunityNoticeDataObj(
    @SerializedName("notice")var notice :List<CommunityNoticeItem>,
    @SerializedName("listSize")var listSize :Int,
    @SerializedName("nextId")var nextId :Int
)

data class CommunityNoticeItem(
    @SerializedName("postId")var postId :Int,
    @SerializedName("title")var title :String,
    @SerializedName("topYn")var topYN :Boolean,
    @SerializedName("userNick")var userNick :String,
    @SerializedName("userPhoto")var userPhoto : String,
    @SerializedName("createDate")var createDate : String
)