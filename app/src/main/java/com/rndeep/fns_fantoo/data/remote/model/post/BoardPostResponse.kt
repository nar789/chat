package com.rndeep.fns_fantoo.data.remote.model.post

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList

data class BoardPostResponse (
    @SerializedName("code") val code :String,
    @SerializedName("msg") val msg:String,
    @SerializedName("dataObj") val dataObj: BoardPostDataObj
)

data class BoardPostDataObj(
    @SerializedName("post")val post : List<BoardPostData>,
    @SerializedName("listSize")val listSize : Int,
    @SerializedName("nextId") val nextId :Int
)

@Entity(tableName = "post_data_tbl")
data class BoardPostData(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("dbID") val pkID :Long=0,
    @SerializedName("postId") val postId :Int,
    @SerializedName("code") val code :String,
    @SerializedName("subCode") val subCode :String?,
    @SerializedName("title") val title :String?,
    @ColumnInfo(name = "integUid")
    @SerializedName("integUid") val integUid :String,
    @SerializedName("langCode") val langCode :String,
    @SerializedName("activeStatus") var activeStatus :Int,
    @SerializedName("content") val content : String?,
    @SerializedName("likeCnt") var likeCnt :Int?,
    @SerializedName("dislikeCnt") var dislikeCnt :Int?,
    @SerializedName("honorCnt") var honorCnt :Int?,
    @SerializedName("replyCnt") var replyCnt :Int?,
    @SerializedName("attachYn") var attachYn :Boolean?,
    @SerializedName("anonymYn") var anonymYn :Boolean?,
    @SerializedName("likeYn") var likeYn :Boolean?,
    @SerializedName("dislikeYn") var dislikeYn :Boolean?,
    @SerializedName("userNick") val userNick :String?,
    @SerializedName("userPhoto") val userPhoto :String?,
    @SerializedName("categoryImage") val categoryImage :String?,
    @SerializedName("createDate") val createDate :String?,
    //임의 추가 데이터
    @SerializedName("userBlockYn") var userBlockYn :Boolean?,
    @SerializedName("pieceBlockYn") var pieceBlockYn :Boolean?,
    @SerializedName("attachList") val attachList : List<DetailAttachList>?,
    @SerializedName("clubName") val clubName : String?,
    @SerializedName("boardName") val boardName : String?,
    @SerializedName("honorYn") var honorYn : Boolean?,

    ) : PostListData()