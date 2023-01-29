package com.rndeep.fns_fantoo.data.remote.model.fantooclub

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FantooClubPost(
    @SerializedName("attachList") val attachList: List<FantooClubPostAttach>?,
    @SerializedName("attachType") val attachType: Int?,
    @SerializedName("blockType") val blockType: String?,
    @SerializedName("boardType") val boardType: Int?,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("categoryName1") val categoryName1: String,
    @SerializedName("categoryName2") val categoryName2: String,
    @SerializedName("clubId") val clubId: String,
    @SerializedName("clubName") val clubName: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("createDate") val createDate: String,
    @SerializedName("deleteType") val deleteType: Int?,
    @SerializedName("firstImage") val firstImage: String?,
    @SerializedName("hashtagList") val hashtagList: List<String>?,
    @SerializedName("honor") val honor: Honor?,
    @SerializedName("integUid") val integUid: String?,
    @SerializedName("langCode") val langCode: String,
    @SerializedName("like") val like: Like?,
    @SerializedName("link") val link: String?,
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("memberLevel") val memberLevel: Int,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("postId") val postId: Int,
    @SerializedName("profileImg") val profileImg: String,
    @SerializedName("replyCount") val replyCount: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("subject") val subject: String?,
    @SerializedName("url") val url: String,
    var translatedTitle: String? = null,
    var translatedBody: String? = null
) : Parcelable

@Parcelize
data class FantooClubPostAttach(
    @SerializedName("attach") var attach: String,
    @SerializedName("attachType") var attachType: Int
) : Parcelable

@Parcelize
data class Honor(
    @SerializedName("honorCount") val honorCount: Int,
    @SerializedName("honorYn") val honorYn: Boolean,
) : Parcelable

@Parcelize
data class Like(
    @SerializedName("likeYn") val likeYn: Boolean?,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("dislikeCount") val dislikeCount: Int,
) : Parcelable