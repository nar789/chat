package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData

data class PostListItemResponse(
    @SerializedName("code") val code :String,
    @SerializedName("msg") val msg:String,
    @SerializedName("dataObj") val dataObj: PostListDataObj
)
data class PostListDataObj(
    @SerializedName("post")val post : List<PostListItem>,
    @SerializedName("listSize")val listSize : Int,
    @SerializedName("nextId") val nextId :Int
)

data class PostListItem(
    @SerializedName("type") val type :String,
    @SerializedName("data") val data : BoardPostData?,
    @SerializedName("clubData") val clubData : ClubPostData?,

)