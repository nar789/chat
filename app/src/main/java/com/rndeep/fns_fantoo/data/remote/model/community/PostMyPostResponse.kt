package com.rndeep.fns_fantoo.data.remote.model.community

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData

data class PostMyPostResponse (
    @SerializedName("code") val code :String,
    @SerializedName("msg") val msg:String,
    @SerializedName("dataObj") val dataObj: PostMyPostDataObj
)

data class PostMyPostDataObj(
    @SerializedName("post")val bookmark : List<BoardPostData>,
    @SerializedName("listSize")val listSize : Int,
    @SerializedName("nextId") val nextId :Int
)