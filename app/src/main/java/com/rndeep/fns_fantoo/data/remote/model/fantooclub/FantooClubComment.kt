package com.rndeep.fns_fantoo.data.remote.model.fantooclub

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FantooClubComment(
    @SerializedName("attachList") val attaches: List<FantooClubPostAttach>,
    @SerializedName("blockType") val blockType: String?,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("categoryName1") val categoryName1: String?,
    @SerializedName("categoryName2") val categoryName2: String?,
    @SerializedName("clubId") val clubId: String,
    @SerializedName("clubName") val clubName: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("createDate") val createDate: String,
    @SerializedName("deleteType") val deleteType: Int,
    @SerializedName("depth") val depth: Int,
    @SerializedName("integUid") val integUid: String?,
    @SerializedName("langCode") val langCode: String,
    @SerializedName("level") val level: Int,
    @SerializedName("like") val like: Like,
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("parentReplyId") val parentReplyId: Int,
    @SerializedName("postId") val postId: Int,
    @SerializedName("profileImg") val profileImg: String?,
    @SerializedName("replyCount") val replyCount: Int,
    @SerializedName("replyId") val replyId: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("subject") val subject: String?,
    @SerializedName("updateDate") val updateDate: String?,
    @SerializedName("url") val url: String?,
    var translatedContent: String? = null
) : Parcelable