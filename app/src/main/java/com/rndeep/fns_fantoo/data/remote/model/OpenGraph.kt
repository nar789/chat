package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class OpenGraph(
    @field:SerializedName("description") val description: String,
    @field:SerializedName("domain") val domain: Boolean,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("url") val url: String
)