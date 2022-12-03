package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoInfo(
    var id: String = "",
    var startSecond: Long = 0L
) : Parcelable
