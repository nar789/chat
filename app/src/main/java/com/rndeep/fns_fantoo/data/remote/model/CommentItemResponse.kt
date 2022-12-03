package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CommentItemResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("msg") val msg: String,
    @field:SerializedName("dataObj") val dataObj: List<CommentItem>,
)

data class CommentItem(
    val profileThumbnail :String?,
    val nickName : String,
    val createDate : Long,
    var commentContent:String,
    var likeCount : Int,
    var commentCount :Int,
    var isMyLike :Boolean,
    var isMyDisLike :Boolean,
    var commentImageList :List<String>?,
    var replyList : List<CommentReply>?
) : Serializable

data class CommentReply(
    val profileThumbnail :String?,
    val nickName : String,
    val createDate : Long,
    var commentContent:String,
    var likeCount : Int,
    var isMyLike :Boolean,
    var isMyDisLike :Boolean,
    var commentImageList : List<String>?
) : Serializable