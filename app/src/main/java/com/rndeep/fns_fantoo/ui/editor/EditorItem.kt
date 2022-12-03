package com.rndeep.fns_fantoo.ui.editor

import okhttp3.MultipartBody
import java.net.URL

data class EditorItem(
    var title: String,
    var content: String,
    val attachedItem: AttachedItem?,
    val isAnonymous: Boolean
)

data class AttachedItem(
    val multimedia: MultimediaItem?,
    val hashtag: HashtagItem?,
)

data class MultimediaItem(
    var url: String?,
    val type: MultimediaType
)

data class MultimediaFile(
    var file : MultipartBody.Part,
    val type : MultimediaType,
    val fileUrl : String
)

data class CloudFlareItem(
    val success : Boolean,
    val index : Int,
    var cloudFlareID : String?,
    val type : MultimediaType
)

data class HashtagItem(
    val hashtags: MutableList<String>,
)

enum class MultimediaType {
    IMAGE, VIDEO
}

