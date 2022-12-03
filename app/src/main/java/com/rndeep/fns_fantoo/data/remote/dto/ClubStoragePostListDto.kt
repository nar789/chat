package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClubStoragePostListDto(
    @field:SerializedName("postList") val postList: List<PostDto>,
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int?
)

data class PostDto(
    @field:SerializedName("attachList") val attachList: List<AttachDto>?,
    @field:SerializedName("attachType") val attachType: Int,
    @field:SerializedName("blockType") val blockType: String?,
    @field:SerializedName("boardType1") val boardType1: Int,
    @field:SerializedName("boardType2") val boardType2: Int,
    @field:SerializedName("categoryCode") val categoryCode: String,
    @field:SerializedName("categoryName1") val categoryName1: String,
    @field:SerializedName("categoryName2") val categoryName2: String,
    @field:SerializedName("clubId") val clubId: String,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("content") val content: String?,
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("deleteType") val deleteType: Int,
    @field:SerializedName("dislikeCount") val dislikeCount: Int,
    @field:SerializedName("firstImage") val firstImage: String,
    @field:SerializedName("hashtagList") val hashtagList: List<String>?,
    @field:SerializedName("honor") val honor: HonorDto,
    @field:SerializedName("honorCount") val honorCount: Int,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("langCode") val langCode: String,
    @field:SerializedName("like") val like: LikeDto,
    @field:SerializedName("likeCount") val likeCount: Int,
    @field:SerializedName("memberId") val memberId: Int,
    @field:SerializedName("memberLevel") val memberLevel: Int,
    @field:SerializedName("memberUrl") val memberUrl: String,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("postId") val postId: Int,
    @field:SerializedName("profileImg") val profileImg: String,
    @field:SerializedName("replyCount") val replyCount: Int,
    @field:SerializedName("status") val status: Int,
    @field:SerializedName("subject") val subject: String?,
    @field:SerializedName("url") val url: String
)

data class HonorDto(
    @field:SerializedName("honorCount") val honorCount: Int,
    @field:SerializedName("honorYn") val honorYn: Boolean
)

data class ClubStoragePostListWithMeta(
    val postDto : PostDto?,
    val listSize: Int
)



