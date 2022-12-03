package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName

data class AlarmConfigDto(
    @field:SerializedName("alimClubConfigDtoList") val alimClubConfigList: List<AlarmConfig>,
    @field:SerializedName("alimConfigId") val alimConfigId: Int,
    @field:SerializedName("comAgree") val comAgree: Boolean,
)

data class AlarmConfig(
    @field:SerializedName("alimClubConfigDtoList") val alimClubConfigId: Int,
    @field:SerializedName("clubAgree") val clubAgree: Boolean,
    @field:SerializedName("clubId") val clubId: Int,
    @field:SerializedName("clubName") val clubName: String,
)

data class ClubAlarmStateDto(
    @field:SerializedName("clubAgree") val clubAgree: Boolean?,
    @field:SerializedName("comAgree") val comAgree: Boolean?
)