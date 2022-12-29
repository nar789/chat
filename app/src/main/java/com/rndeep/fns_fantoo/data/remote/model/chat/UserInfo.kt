package com.rndeep.fns_fantoo.data.remote.model.chat

data class ChatSearchResult(
    var followList: List<ChatUserInfo>? = null,
    var fantooList: List<ChatUserInfo>? = null
)