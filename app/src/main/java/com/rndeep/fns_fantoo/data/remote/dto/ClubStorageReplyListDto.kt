package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClubStorageReplyListDto(
    @field:SerializedName("replyList") val replyList: List<ClubStorageReplyDto>,
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int?
)

data class ClubStorageReplyDto(
    @field:SerializedName("attachList") val attachList: List<AttachDto>,
    @field:SerializedName("blockType") val blockType: String,
    @field:SerializedName("replyId") val replyId: Int,
    @field:SerializedName("parentReplyId") val parentReplyId: Int,
    @field:SerializedName("clubId") val clubId: String,
    @field:SerializedName("content") val content: String?,
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("replyCount") val replyCount: Int,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("status") val status: Int,
    @field:SerializedName("profileImg") val profileImg: String,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("subject") val subject: String?,
    @field:SerializedName("categoryCode") val categoryCode: String,
    @field:SerializedName("categoryName1") val categoryName1: String?,
    @field:SerializedName("categoryName2") val categoryName2: String?,
    @field:SerializedName("deleteType") val deleteType: Int,
    @field:SerializedName("depth") val depth: Int,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("langCode") val langCode: String,
    @field:SerializedName("level") val level: Int,
    @field:SerializedName("like") val likeInfo: LikeDto,
    @field:SerializedName("postId") val postId: Int,
    @field:SerializedName("updateDate") val updateDate: String,
    @field:SerializedName("url") val url: String,
)

data class ClubStorageReplyListWithMeta(
    val clubStorageReplyDto : ClubStorageReplyDto?,
    val listSize : Int
)

data class AttachDto(
    @field:SerializedName("attach") val attach: String,
    @field:SerializedName("attachId") val attachId: Int,
    @field:SerializedName("attachType") val attachType: Int
)

data class LikeDto(
    @field:SerializedName("dislikeCount") val dislikeCount: Int,
    @field:SerializedName("dislikeYn") val dislikeYn: Boolean,
    @field:SerializedName("likeCount") val likeCount: Int,
    @field:SerializedName("likeYn") val likeYn: Boolean
)