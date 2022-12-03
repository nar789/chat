package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.editor.Attach
import com.rndeep.fns_fantoo.data.remote.model.editor.Hashtag

data class ComposeCommunityPost(
    @field:SerializedName("anonymYn") var anonymYn: Boolean,
    @field:SerializedName("attachList") var attaches: List<Attach>?,
    @field:SerializedName("content") var content: String,
    @field:SerializedName("hashtagList") var hashtags: List<Hashtag>?,
    @field:SerializedName("integUid") val integUid: String,
    @field:SerializedName("subCode") var subCode: String?,
    @field:SerializedName("title") var title: String,
)
