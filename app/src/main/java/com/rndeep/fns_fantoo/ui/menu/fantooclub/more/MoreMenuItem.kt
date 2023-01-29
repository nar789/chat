package com.rndeep.fns_fantoo.ui.menu.fantooclub.more

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MoreMenuItem(
    @DrawableRes var icon: Int,
    @StringRes val title: List<Int>,
    var selected: Boolean
)
