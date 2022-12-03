package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class ClubBasicInfo(
    @SerializedName("clubId")val clubId : String,
    @SerializedName("clubName")val clubName : String,
    @SerializedName("introduction")val introduction : String,
    @SerializedName("memberListOpenYn")val memberListOpenYn : Boolean,
    @SerializedName("memberCountOpenYn")val memberCountOpenYn : Boolean,
    @SerializedName("createDate")val createDate : String,
    @SerializedName("profileImg")val profileImg : String,
    @SerializedName("bgImg")val bgImg : String,
    @SerializedName("memberCount")val memberCount : Int,
    @SerializedName("clubMasterNickname")val clubMasterNickname : String,
//    임의 추가 데이터
    @SerializedName("favoriteYN")var favoriteYN : Boolean,
    @SerializedName("isMember")var isMember : Boolean,
    @SerializedName("memberId")var memberId : Int?,
    @SerializedName("memberLevel")var memberLevel : Int,
    @SerializedName("clubJoinState")var clubJoinState : Int

)