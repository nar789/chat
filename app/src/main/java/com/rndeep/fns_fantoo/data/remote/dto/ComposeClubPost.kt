package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.editor.AttachClub

data class ComposeClubPost(
    @field:SerializedName("attachList") var attaches: List<AttachClub>?,
    @field:SerializedName("content") var content: String,
    @field:SerializedName("hashtagList") var hashtags: List<String>?,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("link") val link: String?,
    @field:SerializedName("subject") var subject: String?,
    @field:SerializedName("topYn") var topYn: Boolean,
)
