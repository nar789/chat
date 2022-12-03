package com.rndeep.fns_fantoo.ui.menu.fantooclub.info

import androidx.annotation.DrawableRes

data class DialogInfo(
    val title: String,
    val info: String,
    val followerCnt: String,
    var isFollow: Boolean,
    val isLogin: Boolean,
    @DrawableRes val logo: Int
)
