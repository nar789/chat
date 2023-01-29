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
    @field:SerializedName("introduction") val introduction: String?,
    @field:SerializedName("memberCountOpenYn") val memberCountOpenYn: Boolean,
    @field:SerializedName("memberListOpenYn") val memberListOpenYn: Boolean,
    @field:SerializedName("openYn") val openYn: Boolean,
    @field:SerializedName("interestCategoryCode") val interestCategoryCode: String?,
    @field:SerializedName("languageCode") val languageCode: String,
    @field:SerializedName("activeCountryCode") val activeCountryCode: String,
    @field:SerializedName("memberJoinAutoYn") val memberJoinAutoYn: Boolean,
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("visitDate") val visitDate: String?,
    @field:SerializedName("profileImg") val profileImg: String,
    @field:SerializedName("bgImg") val bgImg: String,
    @field:SerializedName("memberCount") val memberCount: Int,
    @field:SerializedName("clubMasterNickname") val clubMasterNickname: String,
    @field:SerializedName("hashtagList") val hashtagList: List<String>,
    @field:SerializedName("joinMemberCount") val joinMemberCount: Int,
    @field:SerializedName("joinStatus") val joinStatus: Int,
    @field:SerializedName("postCount") val postCount: Int,
) {
    fun toClubAllInfoPacerable(): ClubAllInfoPacerable {
        return ClubAllInfoPacerable(
            clubId = clubId,
            clubName = clubName,
            introduction = introduction,
            memberCountOpenYn = memberCountOpenYn,
            memberListOpenYn = memberListOpenYn,
            openYn = openYn,
            interestCategoryCode = interestCategoryCode,
            languageCode = languageCode,
            activeCountryCode = activeCountryCode,
            memberJoinAutoYn = memberJoinAutoYn,
            createDate = createDate,
            visitDate = visitDate,
            profileImg = profileImg,
            bgImg = bgImg,
            memberCount = memberCount,
            clubMasterNickname = clubMasterNickname,
            hashtagList = hashtagList,
            joinMemberCount = joinMemberCount,
            joinStatus = joinStatus,
            postCount = postCount
        )
    }
}

@Parcelize
data class ClubAllInfoPacerable(
    val clubId: Int,
    val clubName: String,
    val introduction: String?,
    val memberCountOpenYn: Boolean,
    val memberListOpenYn: Boolean,
    val openYn: Boolean,
    val interestCategoryCode: String?,
    val languageCode: String,
    val activeCountryCode: String,
    val memberJoinAutoYn: Boolean,
    val createDate: String,
    val visitDate: String?,
    val profileImg: String,
    val bgImg: String,
    val memberCount: Int,
    val clubMasterNickname: String,
    val hashtagList: List<String>,
    val joinMemberCount: Int,
    val joinStatus: Int,
    val postCount: Int
) : Parcelable

