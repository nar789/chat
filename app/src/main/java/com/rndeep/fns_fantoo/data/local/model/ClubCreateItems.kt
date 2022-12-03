package com.rndeep.fns_fantoo.data.local.model

import okhttp3.MultipartBody
import java.io.Serializable

data class SettingItem(
    var settingType : Int,
    var settingTitle : String,
    var currentSetting : String,
)

data class ClubCreateSendItem(
    val bannerImage :MultipartBody.Part?,
    val bannerDefault : String?,
    val profileImage :MultipartBody.Part?,
    val profileDefault : String?,
    val clubName : String,
    val checkToken : String,
    val acceptMethod : Boolean,
    val openMethod :Boolean,
    val majorLang :String,
    val majorCountry :String
) :Serializable

