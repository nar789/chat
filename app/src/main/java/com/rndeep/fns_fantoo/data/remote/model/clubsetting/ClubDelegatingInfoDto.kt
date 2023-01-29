package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData

data class ClubDelegatingInfoDto(
    @field:SerializedName("createDate") val createDate: String,
    @field:SerializedName("delegateCompleteDate") val delegateCompleteDate: String,
    @field:SerializedName("delegateOkDate") val delegateOkDate: String,
    @field:SerializedName("delegateRequestDate") val delegateRequestDate: String,
    @field:SerializedName("delegateStatus") val delegateStatus: Int,
    @field:SerializedName("expectCancelDate") val expectCancelDate: String?,
    @field:SerializedName("expectCompleteDate") val expectCompleteDate: String?,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("joinDate") val joinDate: String,
    @field:SerializedName("joinStatus") val joinStatus: Int,
    @field:SerializedName("memberId") val memberId: Int,
    @field:SerializedName("memberLevel") val memberLevel: Int,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("nicknameChangeDate") val nicknameChangeDate: String,
    @field:SerializedName("profileImg") val profileImg: String,
    @field:SerializedName("visitDate") val visitDate: String
)

data class ClubDelegatingInfoResponse(
    val code: String?,
    val msg: String?,
    val clubDelegatingInfoDto : ClubDelegatingInfoDto?,
    val errorData: ErrorData?
)
