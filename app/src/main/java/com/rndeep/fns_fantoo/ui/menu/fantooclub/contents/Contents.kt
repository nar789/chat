package com.rndeep.fns_fantoo.ui.menu.fantooclub.contents

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contents(
    val id: Long = 0L,
    var createdAt: Long = 0L,
    var category: String = "",
    var title: String = "",
    var body: String? = null,
    var logo: String? = null,
    var imageThumbnail: String? = null,
    var videoThumbnail: String? = null,
    var hashtag: List<String>? = null,
    var link: String? = null,
    var like: Int = 0,
    var comments: Int? = null,
    var honor: Int = 0,
    var saved: Boolean = false
): Parcelable
