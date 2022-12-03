package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class MyClubResponse(
    @SerializedName("clubCount") val clubCount : Int,
    @SerializedName("clubList") val clubList : List<MyClubListItem>,
    @SerializedName("favoriteYn") val favoriteYn : Boolean?,
    @SerializedName("listSize") val listSize : Int,
    @SerializedName("nextId") val nextId : String?,
)

data class MyClubListItem(
    @SerializedName("clubId") val clubId : String,
    @SerializedName("clubName") val clubName : String,
    @SerializedName("favoriteYn") val favoriteYn : Boolean,
    @SerializedName("manageYn") val manageYn : Boolean,
    @SerializedName("memberCount") val memberCount : Int,
    @SerializedName("openYn") val openYn : Boolean,
    @SerializedName("profileImg") val profileImg : String,
    @SerializedName("visitDate") val visitDate : String,
//    임의 추가 데이터 기획상에는 있고 데이터 상에는 존재하지않아 임의 추가
    @SerializedName("isOwner") val isOwner : Boolean
)