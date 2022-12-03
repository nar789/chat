package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.HonorItem

sealed class PostDetailData

data class BoardPostDetailResponse(
    @SerializedName("post") val post : BoardPostDetailData,
    @SerializedName("openGraphList") val openGraphList : List<Any>?,
    @SerializedName("attachList") var attachList: List<DetailAttachList>,
    @SerializedName("hashtagList") var hashtagList: List<BoardPostDetailHashTag>
)

data class BoardPostDetailData(
    @SerializedName("postId") val postId :Int,
    @SerializedName("code") val code :String,
    @SerializedName("subCode") val subCode : String,
    @SerializedName("title") var title:String?,
    @SerializedName("content") var content : String?,
    @SerializedName("integUid") val integUid : String,
    @SerializedName("langCode") val langCode : String,
    @SerializedName("activeStatus") var activeStatus :Int?,
    @SerializedName("likeCnt") var likeCnt :Int?,
    @SerializedName("dislikeCnt") var dislikeCnt :Int?,
    @SerializedName("honorCnt") var honorCnt :Int?,
    @SerializedName("replyCnt") var replyCnt :Int?,
    @SerializedName("anonymYn") var anonymYn :Boolean,
    @SerializedName("likeYn") var myLikeYn : Boolean?,
    @SerializedName("dislikeYn") var myDisLikeYn : Boolean?,
    @SerializedName("userBlockYn") var userBlockYn : Boolean?,
    @SerializedName("pieceBlockYn") var pieceBlockYn : Boolean?,
    @SerializedName("userNick") val userNick :String?,
    @SerializedName("userPhoto") val userPhoto : String?,
    @SerializedName("createDate") val createDate :String?,
    @SerializedName("bookmarkYn") var bookmarkYn : Boolean,
//    아래 데이터는 임의 추가된 데이터임!
    @SerializedName("myHonorYn") var myHonorYn : Boolean?,
    @SerializedName("translateYn") var translateYn : Boolean?,
) :PostDetailData()

data class ClubPostDetailData(
    @SerializedName("integUid") val integUid :String,
    @SerializedName("postId") val postId :Int,
    @SerializedName("clubId") val clubId :String,
    @SerializedName("memberId") val memberId :Int,
    @SerializedName("categoryCode") val categoryCode :String,
    @SerializedName("subject") var subject :String,
    @SerializedName("content") var content :String,
    @SerializedName("langCode") val langCode :String,
    @SerializedName("createDate") val createDate :String,
    @SerializedName("attachList") val attachList : List<ClubPostAttachList>?,
    @SerializedName("hashtagList") val hashtagList : List<String>?,
    @SerializedName("replyCount") val replyCount : Int,
    @SerializedName("honorCount") val honorCount : Int?,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("memberLevel") val memberLevel : Int,
    @SerializedName("url") val url : String,
    @SerializedName("memberUrl") val memberUrl : String,
    @SerializedName("categoryName1") val categoryName1 : String,
    @SerializedName("categoryName2") val categoryName2 : String,
    @SerializedName("status") val status : Int,
    @SerializedName("blockType") val blockType : Int?,
    @SerializedName("profileImg") val profileImg : String?,
    @SerializedName("honor") val honor : HonorItem?,
    @SerializedName("attachType") val attachType : Int?,
    @SerializedName("deleteType") val deleteTyep :Int,
    //    아래 데이터는 임의 추가된 데이터임!
    @SerializedName("myHonorYn") var myHonorYn : Boolean?,
    @SerializedName("translateYn") var translateYn : Boolean?,
    @SerializedName("isBookmarkYn") var isBookmarkYn : Boolean?
) : PostDetailData()

data class DetailAttachList(
    @SerializedName("attachType") val archiveType :String,
    @SerializedName("id") val id : String?
)

data class BoardPostDetailHashTag(
    @SerializedName("tag")val tagText :String
)