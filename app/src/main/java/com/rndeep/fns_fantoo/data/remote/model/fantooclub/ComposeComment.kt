package com.rndeep.fns_fantoo.data.remote.model.fantooclub

import com.google.gson.annotations.SerializedName

data class ComposeComment(
    @SerializedName("attachList") val attaches: List<FantooClubPostAttach>?,
    @SerializedName("content") val content: String,
    @SerializedName("integUid") val integUid: String?,
    @SerializedName("langCode") val langCode: String?,
)
