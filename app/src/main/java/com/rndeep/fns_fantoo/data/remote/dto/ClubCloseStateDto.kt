package com.rndeep.fns_fantoo.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ClubCloseStateDto(
    @SerializedName("closesDate") val closesDate :String,
    @SerializedName("closesRequestDate") val closesRequestDate :String,
    @SerializedName("closesStatus") val closesStatus :Int,
    @SerializedName("clubId") val clubId :Int
){
    fun toClubCloseStateInfoPacerable():ClubCloseStateInfoPacerable{
        return ClubCloseStateInfoPacerable(
            closesDate = closesDate,
            closesRequestDate = closesRequestDate,
            closesStatus = closesStatus,
            clubId = clubId
        )
    }
}

@Parcelize
data class ClubCloseStateInfoPacerable(
    val closesDate :String,
    val closesRequestDate :String,
    val closesStatus :Int,
    val clubId :Int
): Parcelable