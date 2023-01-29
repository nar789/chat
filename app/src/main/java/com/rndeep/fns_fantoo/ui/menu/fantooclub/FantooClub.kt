package com.rndeep.fns_fantoo.ui.menu.fantooclub

data class FantooClub(
    val id: Long = 0L,
    val logo: String?,
    val bg: String?,
    val title: String = "",
    var description: String = "",
    var followCount: Int = 0,
    var isFollowed: Boolean = false,
)
