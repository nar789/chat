package com.rndeep.fns_fantoo.data.remote.model.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ModifyCommunityPost(
    var anonymYn: Boolean,
    var attaches: List<Attach>?,
    var content: String,
    var hashtags: List<Hashtag>?,
    val integUid: String,
    var subCode: String?,
    var title: String,
    val postId: Int,
    val boardCode: String
) : Parcelable
