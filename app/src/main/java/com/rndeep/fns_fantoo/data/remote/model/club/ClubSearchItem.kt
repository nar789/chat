package com.rndeep.fns_fantoo.data.remote.model.club

import com.google.gson.annotations.SerializedName

data class ClubSearchItem(
    @SerializedName("activeCountryCode") val activeCountryCode :String,
    @SerializedName("bgImg") val bgImg :String,
    @SerializedName("clubId") val clubId :String,
    @SerializedName("clubMasterNickname") val clubMasterNickname :String,
    @SerializedName("clubName") val clubName :String,
    @SerializedName("createDate") val createDate :String,
    @SerializedName("hashtagList") val hashtagList :List<String>,
    @SerializedName("interestCategoryCode") val interestCategoryCode :String,
    @SerializedName("introduction") val introduction :String,
    @SerializedName("joinMemberCount") val joinMemberCount :Int,
    @SerializedName("languageCode") val languageCode :String,
    @SerializedName("memberCount") val memberCount :Int,
    @SerializedName("memberCountOpenYn") val memberCountOpenYn :Boolean,
    @SerializedName("memberJoinAutoYn") val memberJoinAutoYn :Boolean,
    @SerializedName("memberListOpenYn") val memberListOpenYn :Boolean,
    @SerializedName("openYn") val openYn :Boolean,
    @SerializedName("postCount") val postCount :Int,
    @SerializedName("profileImg") val profileImg :String,
)