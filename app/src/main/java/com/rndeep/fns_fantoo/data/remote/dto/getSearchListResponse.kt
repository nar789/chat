package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GetUserListResponse(
    @field:SerializedName("chatUserDtoList") val chatUserDtoList: List<ChatUserDto>,
    @field:SerializedName("listSize") val listSize: Int,
    @field:SerializedName("nextId") val nextId: Int,
){
    data class ChatUserDto(
        @field:SerializedName("type") val type: String,
        @field:SerializedName("userNick") val userNick: String,
        @field:SerializedName("userPhoto") val userPhoto: String?,
    ) {
        companion object {
            const val TYPE_FOLLOW = "follow"
            const val TYPE_OTHER = "other"
        }

        fun isFollow() = type == TYPE_FOLLOW
        fun isOther() = type == TYPE_OTHER
    }
}
