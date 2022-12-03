package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.CommunityOpenGraphItem
import java.io.Serializable

sealed class PostReplyData

data class CommunityReplyDataObj(
    @SerializedName("reply") var reply : List<CommunityReplyData>,
    @SerializedName("listSize") val listSize : Int,
    @SerializedName("nextId") val nextId :Int
)

data class ClubReplyDataObj(
    @SerializedName("replyList") var reply : List<ClubReplyData>,
    @SerializedName("listSize") val listSize : Int,
    @SerializedName("nextId") val nextId :String
)

data class CommunityReplyData(
    @SerializedName("replyId") val replyId : Int,
    @SerializedName("parentReplyId") val parentReplyId : Int,
    @SerializedName("comPostId") val comPostId : Int,
    @SerializedName("integUid") val integUid : String,
    @SerializedName("content") var content : String,
    @SerializedName("activeStatus") val activeStatus : Int,
    @SerializedName("anonymYn") val anonymYn : Boolean,
    @SerializedName("depth") val depth : Int,
    @SerializedName("langCode") val langCode : String,
    @SerializedName("likeCnt") var likeCnt :Int?,
    @SerializedName("dislikeCnt") var dislikeCnt :Int?,
    @SerializedName("replyCnt") var replyCnt :Int?,
    @SerializedName("likeYn") var myLikeYn :Boolean?,
    @SerializedName("dislikeYn") var myDisLikeYn :Boolean?,
    @SerializedName("userNick") val userNick :String?,
    @SerializedName("userPhoto") val userPhoto :String?,
    @SerializedName("userBlockYn") var userBlockYn :Boolean?,
    @SerializedName("pieceBlockYn") var pieceBlockYn :Boolean?,
    @SerializedName("attachList") val attachList : List<DetailAttachList>?,
    @SerializedName("openGraphList") val openGraphList : List<CommunityOpenGraphItem>?,
    @SerializedName("childReplyList") val childReplyList : List<CommunityReplyData>?,
    @SerializedName("createDate") val createDate :String,
    //임의 추가
    @SerializedName("translateYN") var translateYn :Boolean?,
) : Serializable, PostReplyData()

data class ClubReplyData(
    @SerializedName("integUid") val integUid : String,
    @SerializedName("replyId") val replyId : Int,
    @SerializedName("parentReplyId") val parentReplyId : Int,
    @SerializedName("postId") val postId : Int,
    @SerializedName("clubId") val clubId : String,
    @SerializedName("memberId") val memberId : Int,
    @SerializedName("content") var content : String,
    @SerializedName("langCode") val langCode : String,
    @SerializedName("createDate") val createDate : String,
    @SerializedName("replyCount") val replyCount : Int,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("level") val level : Int,
    @SerializedName("status") val status : Int,
    @SerializedName("depth") val depth : Int,
    @SerializedName("profileImg") val profileImg : String,
    @SerializedName("categoryCode") val categoryCode : String?,
    @SerializedName("deleteType") val deleteType : Int,
    @SerializedName("blockType") val blockType : Int?,
    //임의 추가 데이터
    @SerializedName("attachList") val attachList : List<ClubPostAttachList>?,
    @SerializedName("translateYN") var translateYn : Boolean?,
    @SerializedName("childReplyList") var childReplyList : ArrayList<ClubReplyData>?,
    @SerializedName("openGraphList") var openGraphItem : ArrayList<CommunityOpenGraphItem>?,
) : PostReplyData()