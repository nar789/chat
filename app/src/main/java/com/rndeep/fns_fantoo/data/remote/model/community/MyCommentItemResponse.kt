package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.post.CommunityOpenGraphItem

data class MyCommentItemResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("msg") val msg: String,
    @field:SerializedName("dataObj") val dataObj: MyCommentItemDataObj
)

data class MyCommentItemDataObj(
    @SerializedName("reply") val reply : List<MyCommentItem>,
    @SerializedName("listSize") val listSize : Int,
    @SerializedName("nextId") val nextId : Int,
)
data class MyCommentItem(
    @SerializedName("replyId") val replyId : String,
    @SerializedName("parentReplyId") val parentReplyId : Int,
    @SerializedName("comPostId") val comPostId : Int,
    @SerializedName("integUid") val integUid : String,
    @SerializedName("content") val content : String,
    @SerializedName("activeStatus") val activeStatus : Int,
    @SerializedName("anonymYn") val anonymYn : Boolean,
    @SerializedName("depth") val depth : Int,
    @SerializedName("langCode") val langCode : String,
    @SerializedName("likeCnt") val likeCnt : Int,
    @SerializedName("dislikeCnt") val dislikeCnt : Int,
    @SerializedName("replyCnt") val replyCnt : Int,
    @SerializedName("likeYn") val likeYn : Boolean,
    @SerializedName("dislikeYn") val dislikeYn : Boolean,
    @SerializedName("userNick") val userNick : String,
    @SerializedName("userPhoto") val userPhoto : String,
    @SerializedName("postTitle") val postTitle : String,
    @SerializedName("code") val code : String,
    @SerializedName("image") val image : String,
    @SerializedName("userBlockYn") val userBlockYn : Boolean,
    @SerializedName("pieceBlockYn") val pieceBlockYn : Boolean,
    @SerializedName("attachList") val attachList : List<DetailAttachList>?,
    @SerializedName("openGraphList") val openGraphList : List<CommunityOpenGraphItem>?,
    @SerializedName("createDate") val createDate : String,
)