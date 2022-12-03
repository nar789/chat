package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName

data class BookmarkResponse(
    @SerializedName("connection") val connection :String,
    @SerializedName("content-length") val contentLength :String,
    @SerializedName("date") val date: String,
    @SerializedName("keep-alive") val keepAlive: String
)
