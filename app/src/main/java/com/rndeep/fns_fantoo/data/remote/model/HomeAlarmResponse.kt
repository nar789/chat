package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class HomeAlarmResponse(
    @SerializedName("alimList") val alimList: List<HomeAlarmData>,
    @SerializedName("listSize") val listSize: Int,
    @SerializedName("nextId") val nextId: Int
)

data class HomeAlarmData(
    @SerializedName("alimId") var alimId: Int,
    @SerializedName("alarmType") var alarmType :String,
    @SerializedName("alarmThumbnail") var alarmThumbnail: String?,
    @SerializedName("title") var title: String?,
    @SerializedName("body") var body :String,
    @SerializedName("message") var message :String?,
    @SerializedName("alarmSender") var alarmSender: String,
    @SerializedName("date") var alarmSendDate : String,
    @SerializedName("isRead") var isRead : Boolean?
)