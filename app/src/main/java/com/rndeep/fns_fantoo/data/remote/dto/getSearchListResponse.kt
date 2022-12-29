package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GetUserListResponse(
    @field:SerializedName("chatUserDtoList") var chatUserDtoList: List<ChatUserDto>,
    @field:SerializedName("listSize") var listSize: Int,
    @field:SerializedName("nextId") var nextId: Int,
){
    data class ChatUserDto(
        @field:SerializedName("type") var type: String,
        @field:SerializedName("userNick") var userNick: String,
        @field:SerializedName("userPhoto") var userPhoto: String?,
    )
}
