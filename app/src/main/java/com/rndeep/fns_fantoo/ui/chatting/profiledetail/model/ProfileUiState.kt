package com.rndeep.fns_fantoo.ui.chatting.profiledetail.model

data class ProfileUiState(
    val blocked: Boolean = false,
    val followed: Boolean = false,
    val name: String = "",
    val photo: String? = null
)