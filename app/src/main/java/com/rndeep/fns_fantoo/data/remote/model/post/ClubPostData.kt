package com.rndeep.fns_fantoo.data.remote.model.post

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class ClubPostData (
    @SerializedName("attachList")val attachList : List<ClubPostAttachList>?,
    @SerializedName("attachType")val attachType : Int?,
    @SerializedName("blockType")val blockType : Int?,
    @SerializedName("boardType1")val boardType1 : Int?,
    @SerializedName("boardType2")val boardType2 : Int?,
    @SerializedName("categoryCode")val categoryCode : String,
    @SerializedName("clubId")val clubId : String,
    @SerializedName("clubName")val clubName : String?,
    @SerializedName("categoryName1")val categoryName1 : String?,
    @SerializedName("categoryName2")val categoryName2 : String,
    @SerializedName("content")val content : String?,
    @SerializedName("createDate")val createDate : String,
    @SerializedName("deleteType")val deleteType : Int?,
    @SerializedName("firstImage")val firstImage : String?,
    @ColumnInfo(name = "integUid")
    @SerializedName("integUid")val integUid : String?,
    @SerializedName("langCode")val langCode : String?,
    @SerializedName("honor")val honor : HonorItem?,
    @SerializedName("memberId")val memberId : Int,
    @SerializedName("memberLevel")val memberLevel : Int,
    @SerializedName("memberUrl")val memberUrl : String?,
    @SerializedName("nickname")val nickname : String?,
    @SerializedName("postId")val postId : Int,
    @SerializedName("profileImg")val profileImg : String,
    @SerializedName("replyCount")val replyCount : Int,
    @SerializedName("status")val status : Int,
    @SerializedName("subject")val subject : String?,
    @SerializedName("url")val url : String?
) : PostListData()

data class ClubPostAttachList(
    @SerializedName("attach")val attach : String,
    @SerializedName("attachType")val attachType : Int,
    @SerializedName("attachId")val attachId : Int

)

data class HonorItem(
    @SerializedName("honorCount")val honorCount : Int,
    @SerializedName("honorYn")val honorYn : Boolean,

)
/*
    "attachList": [
        {
          "attach": "string",
          "attachId": 0,
          "attachType": 0
        }
      ],
      "attachType": 0,
      "blockType": "string",
      "boardType1": 0,
      "boardType2": 0,
      "categoryCode": "string",
      "clubId": 0,
      "clubName": "string",
      "codeNameEn1": "string",
      "codeNameEn2": "string",
      "codeNameKo1": "string",
      "codeNameKo2": "string",
      "content": "string",
      "createDate": "2022-09-15T06:01:23.795Z",
      "deleteType": 0,
      "dislikeCount": 0,
      "firstImage": "string",
      "hashtagList": [
        "string"
      ],
      "honor": {
        "honorCount": 0,
        "honorYn": true
      },
      "honorCount": 0,
      "integUid": "string",
      "langCode": "string",
      "like": {
        "dislikeCount": 0,
        "dislikeYn": true,
        "likeCount": 0,
        "likeYn": true
      },
      "likeCount": 0,
      "memberId": 0,
      "memberLevel": 0,
      "memberUrl": "string",
      "nickname": "string",
      "postId": 0,
      "profileImg": "string",
      "replyCount": 0,
      "status": 0,
      "subject": "string",
      "url": "string"
    }


 */