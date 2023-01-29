package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData

data class BoardMainPostResponse (
        @SerializedName("code") val code :String,
        @SerializedName("msg") val msg:String,
        @SerializedName("dataObj") val dataObj: BoardMainPostData
)

data class BoardMainPostData(
    @SerializedName("hour") val hourPost : List<BoardPostData>,
    @SerializedName("week") val weekPost : List<BoardPostData>,

    )