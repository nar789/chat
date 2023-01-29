package com.rndeep.fns_fantoo.data.remote.model.chat

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse

data class CreateChatUserInfo(
    @field:SerializedName("userNick") val userNick: String,
    @field:SerializedName("userPhoto") val userPhoto: String?,
    @field:SerializedName("id") val id: String) {

    companion object {
        fun create(dto: GetUserListResponse.ChatUserDto) = CreateChatUserInfo(
            userNick = dto.userNick,
            userPhoto = dto.userPhoto,
            id = dto.integUid
        )
        
        fun createList(dtos: List<GetUserListResponse.ChatUserDto>) = mutableListOf<CreateChatUserInfo>().apply {
            dtos.forEach {
                add(create(it))
            }
        }
    }
}