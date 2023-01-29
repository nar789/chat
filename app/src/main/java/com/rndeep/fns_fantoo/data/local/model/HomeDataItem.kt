package com.rndeep.fns_fantoo.data.local.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AdInfo(
    @ColumnInfo(name = "ad_id")
    var id :String?="",
    @Embedded
    @SerializedName("contents")
    var adContents: ADContents =ADContents(),
    @Embedded
    @SerializedName("image")
    var ADImage: ADImage = ADImage(),
    @Embedded
    @SerializedName("link")
    var ADLink: ADLink =ADLink(),
    @Embedded
    @SerializedName("title")
    var ADTitle: ADTitle =ADTitle()
)

data class ADContents(
    @ColumnInfo(name = "ad_contents_en")
    var en: String="",
    @ColumnInfo(name = "ad_contents_ko")
    var ko: String=""
)

data class ADImage(
    @ColumnInfo(name = "ad_image_en")
    var en: String="",
    @ColumnInfo(name = "ad_image_ko")
    var ko: String=""
)

data class ADLink(
    @ColumnInfo(name = "ad_subType")
    var subType: String="",
    @ColumnInfo(name = "ad_type")
    var type: String="",
    @ColumnInfo(name = "ad_varue")
    var varue: String=""
)

data class ADTitle(
    @ColumnInfo(name = "ad_title_en")
    var en: String="",
    @ColumnInfo(name = "ad_title_ko")
    var ko: String=""
)

data class PostThumbnail(
    var type: String="",
    var url: String="",
    var title : String?=null,
    var description : String? =null
):Serializable
