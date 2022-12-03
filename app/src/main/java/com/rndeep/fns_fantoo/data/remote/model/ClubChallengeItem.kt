package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.utils.ConstVariable

data class ClubChallengeItem(
    @SerializedName("postId")var id :Int,
    @SerializedName("langCode")var langCode : String,
    @SerializedName("subject")var subject : String,
    @SerializedName("content")var content : String?,
    @SerializedName("createBy")var createBy : String?,
    @SerializedName("createDate")var createDate : String,
    @SerializedName("url")var url : String,
    @SerializedName("status")var status : Int,
    @SerializedName("attachList")var attachList : List<ClubPostAttachList>?,
    @SerializedName("blockType")var blockType : Int,
    @SerializedName("type")var type : String =ConstVariable.CHALLENGE_YES,
)
