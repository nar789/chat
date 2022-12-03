package com.rndeep.fns_fantoo.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData
import kotlinx.parcelize.Parcelize

data class ClubMemberInfoResponse(
    val code: String,
    val msg: String,
    val dataObj: ClubMemberInfoDto?,
    val errorData: ErrorData?
)

data class ClubMemberInfoDto(
    @field:SerializedName("integUid") val integUid: String?,
    @field:SerializedName("memberId") val memberId: Int,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("nicknameChangeDate") val nicknameChangeDate: String,
    @field:SerializedName("memberLevel") val memberLevel: Int,
    @field:SerializedName("profileImg") val profileImg: String?,
    @field:SerializedName("joinStatus") val joinStatus: Int,
    @field:SerializedName("createDate") val createDate: String?,
    @field:SerializedName("visitDate") val visitDate: String?
) {
    fun toClubMemberInfoPacerable(): ClubMemberInfoPacerable {
        return ClubMemberInfoPacerable(
            integUid,
            memberId,
            nickname,
            nicknameChangeDate,
            memberLevel,
            profileImg,
            joinStatus,
            createDate,
            visitDate
        )
    }
}

@Parcelize
data class ClubMemberInfoPacerable(
    val integUid: String?,
    val memberId: Int,
    val nickname: String,
    val nicknameChangeDate: String,
    val memberLevel: Int,
    val profileImg: String?,
    val joinStatus: Int,
    val createDate: String?,
    val visitDate: String?
) : Parcelable

data class ClubMemberInfoWithMeta(
    val clubMemberInfo : ClubMemberInfoDto,
    val memberCount : Int,
    val totalMemberCount : Int,
    val listSize : Int
)