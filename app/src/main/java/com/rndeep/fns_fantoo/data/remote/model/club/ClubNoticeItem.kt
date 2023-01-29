package com.rndeep.fns_fantoo.data.remote.model.club

import com.google.gson.annotations.SerializedName

data class ClubNoticeItem (
    @SerializedName("postId")val postId :Int,
    @SerializedName("clubId")val clubId :String,
    @SerializedName("memberId")val memberId :Int,
    @SerializedName("categoryCode")val categoryCode :String,
    @SerializedName("subject")val subject :String,
    @SerializedName("content")val content :String?,
    @SerializedName("createDate")val createDate :String,
    @SerializedName("nickname")val nickname :String,
    @SerializedName("memberLevel")val memberLevel :Int,
    @SerializedName("url")val url :String,
    @SerializedName("categoryName2")val categoryName2 :String,
    @SerializedName("profileImg")val profileImg :String,
    @SerializedName("boardType2")val boardType2 :Int,
    @SerializedName("deleteType")val deleteType :Int,
)