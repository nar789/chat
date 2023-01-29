package com.rndeep.fns_fantoo.data.remote.model.chat

data class UserListResult(
    val users: List<TmpUserInfo>
)

data class TmpUserInfo(
    val userName: String,
    val userPhoto: String,
    val loginId: String
)

data class ChatSearchResult(
    var followList: List<TmpUserInfo>? = null,
    var fantooList: List<TmpUserInfo>? = null
)