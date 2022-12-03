package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.userinfo.Interest

data class UserInfo(
    @field:SerializedName("loginId") val loginId: String,
    @field:SerializedName("loginPwUpdate") val loginPwUpdate: String?,
    @field:SerializedName("loginType") val loginType: String,
    @field:SerializedName("fnsCoins") val fnsCoins: String?,
    @field:SerializedName("birthDay") var birthDay: String?,
    @field:SerializedName("userPhoto") var userPhoto: String?,
    @field:SerializedName("userNick") var userNick: String?,
    @field:SerializedName("userNickUpdate") val userNickUpdate: String?,
    @field:SerializedName("userName") val userName: String?,
    @field:SerializedName("email") val email: String?,
    @field:SerializedName("cellNum") val cellNum: String?,
    @field:SerializedName("cellCode") val cellCode: String?,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("sortStatus") val sortStatus: String?,
    @field:SerializedName("lastLoginDate") val lastLoginDate: String?,
    @field:SerializedName("genderType") var genderType: String?,
    @field:SerializedName("deviceId") val deviceId: String?,
    @field:SerializedName("referralCode") val referralCode: String?,
    @field:SerializedName("useReferralCode") val useReferralCode: String?,
    @field:SerializedName("introduce") val introduce: String?,
    @field:SerializedName("cdate") val cdate: String?,
    @field:SerializedName("piosToken") val piosToken: String?,
    @field:SerializedName("pandroidToken") val pandroidToken: String?,
    @field:SerializedName("countryIsoTwo") var countryIsoTwo: String?,
    @field:SerializedName("interestList") var interestList: List<Interest>
)

data class User(
    @field:SerializedName("user") val userInfo: UserInfo,
)