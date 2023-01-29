package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.OpenGraph
import com.rndeep.fns_fantoo.data.remote.model.editor.Attach

data class CommunityMyReply(
    @field:SerializedName("activeStatus") val activeStatus: Int,
    @field:SerializedName("anonymYn") val anonymYn: Boolean,
    @field:SerializedName("attachList") val attachList: List<Attach>,
    @field:SerializedName("childReplyList") val childReplyList: List<String>,
    @field:SerializedName("code") val code: String,
    @field:SerializedName("comPostId") val comPostId: Int,
    @field:SerializedName("content") val content: String,
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("dislikeCnt") val dislikeCnt: Int,
    @field:SerializedName("dislikeYn") val dislikeYn: Boolean,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("langCode") val langCode: String,
    @field:SerializedName("likeCnt") val likeCnt: Int,
    @field:SerializedName("likeYn") val likeYn: Boolean,
    @field:SerializedName("openGraphList") val openGraphList: List<OpenGraph>,
    @field:SerializedName("parentReplyId") val parentReplyId: Int,
    @field:SerializedName("pieceBlockYn") val pieceBlockYn: Boolean,
    @field:SerializedName("postTitle") val postTitle: String,
    @field:SerializedName("replyCnt") val replyCnt: Int,
    @field:SerializedName("replyId") val replyId: Int,
    @field:SerializedName("userBlockYn") val userBlockYn: Boolean,
    @field:SerializedName("userNick") val userNick: String,
    @field:SerializedName("userPhoto") val userPhoto: String
)
