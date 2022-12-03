package com.rndeep.fns_fantoo.data.remote.model.post

import com.google.gson.annotations.SerializedName

data class ClubBookmarkResult(
    @SerializedName("bookmarkYn") val bookmarkYn: Boolean,
    @SerializedName("integUid") val integUid: String?,
    @SerializedName("postId") val postId: Int?
)
