package com.rndeep.fns_fantoo.data.remote.model.post

import com.google.gson.annotations.SerializedName

data class CommunityOpenGraphItem(
    @SerializedName("title") val title :String,
    @SerializedName("domain") val domain :String,
    @SerializedName("url") val url :String,
    @SerializedName("image") val image :String,
    @SerializedName("description") val description :String,
)
