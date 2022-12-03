package com.rndeep.fns_fantoo.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData
import kotlinx.parcelize.Parcelize


data class ClubAllInfoResponse(
    val code: String,
    val msg: String,
    val clubAllInfo: ClubAllInfoDto?,
    val errorData: ErrorData?
)

data class ClubAllInfoDto(
    @field:SerializedName("clubId") val clubId: Int,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("introduction") val introduction: String,
    @field:SerializedName("memberCountOpenYn") val memberCountOpenYn: Boolean,
    @field:SerializedName("memberListOpenYn") val memberListOpenYn: Boolean,
    @field:SerializedName("openYn") val openYn: Boolean,
    @field:SerializedName("interestCategoryId") val interestCategoryId: String?,
    @field:SerializedName("languageCode") val languageCode: String,
    @field:SerializedName("activeCountryCode") val activeCountryCode: String,
    @field:SerializedName("memberJoinAutoYn") val memberJoinAutoYn: Boolean,
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("profileImg") val profileImg: String,
    @field:SerializedName("bgImg") val bgImg: String,
    @field:SerializedName("memberCount") val memberCount: Int,
    @field:SerializedName("clubMasterNickname") val clubMasterNickname: String,
) {
    fun toClubAllInfoPacerable(): ClubAllInfoPacerable {
        return ClubAllInfoPacerable(
            clubId = clubId,
            clubName = clubName,
            introduction = introduction,
            memberCountOpenYn = memberCountOpenYn,
            memberListOpenYn = memberListOpenYn,
            openYn = openYn,
            interestCategoryId = interestCategoryId,
            languageCode = languageCode,
            activeCountryCode = activeCountryCode,
            memberJoinAutoYn = memberJoinAutoYn,
            createDate = createDate,
            profileImg = profileImg,
            bgImg = bgImg,
            memberCount = memberCount,
            clubMasterNickname = clubMasterNickname
        )
    }
}

@Parcelize
data class ClubAllInfoPacerable(
    val clubId: Int,
    val clubName: String,
    val introduction: String,
    val memberCountOpenYn: Boolean,
    val memberListOpenYn: Boolean,
    val openYn: Boolean,
    val interestCategoryId: String?,
    val languageCode: String,
    val activeCountryCode: String,
    val memberJoinAutoYn: Boolean,
    val createDate: String,
    val profileImg: String,
    val bgImg: String,
    val memberCount: Int,
    val clubMasterNickname: String,
) : Parcelable

