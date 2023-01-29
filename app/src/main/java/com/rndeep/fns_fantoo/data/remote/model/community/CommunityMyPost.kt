package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName

data class CommunityMyPost(
    @field:SerializedName("activeStatus") val activeStatus: Int,
    @field:SerializedName("anonymYn") val anonymYn: Boolean,
    @field:SerializedName("attachYn") val attachYn: Boolean,
    @field:SerializedName("categoryImage") val categoryImage: String,
    @field:SerializedName("code") val code: String,
    @field:SerializedName("content") val content: String,
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("dislikeCnt") val dislikeCnt: Int,
    @field:SerializedName("dislikeYn") val dislikeYn: Boolean,
    @field:SerializedName("honorCnt") val honorCnt: Int,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("langCode") val langCode: String,
    @field:SerializedName("likeCnt") val likeCnt: Int,
    @field:SerializedName("likeYn") val likeYn: Boolean,
    @field:SerializedName("pieceBlockYn") val pieceBlockYn: Boolean,
    @field:SerializedName("postId") val postId: String,
    @field:SerializedName("replyCnt") val replyCnt: Int,
    @field:SerializedName("subCode") val subCode: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("userBlockYn") val userBlockYn: Boolean,
    @field:SerializedName("userNick") val userNick: String,
    @field:SerializedName("userPhoto") val userPhoto: String
)
