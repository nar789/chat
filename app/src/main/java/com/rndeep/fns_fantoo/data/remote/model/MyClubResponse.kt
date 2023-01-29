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
)