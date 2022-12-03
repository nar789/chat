package com.rndeep.fns_fantoo.data.remote.model.fantooclub

import com.google.gson.annotations.SerializedName

data class ComposeCommentReply(
    @SerializedName("attachList") val attaches: List<FantooClubPostAttach>?,
    @SerializedName("clubPostId") val clubPostId: Int,
    @SerializedName("clubReplyId") val clubReplyId: Int,
    @SerializedName("content") val content: String,
    @SerializedName("integUid") val integUid: String?,
    @SerializedName("langCode") val langCode: String?,
    @SerializedName("parentReplyId") val parentReplyId: Int
)
