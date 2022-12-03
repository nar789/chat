package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class CloudFlareVideo (
    @SerializedName("result")val result : CloudFlareVideoData?,
    @SerializedName("success")val success : Boolean,
    @SerializedName("errors")val errors : List<Any>,
    @SerializedName("messages")val messages : List<Any>?,
)

data class CloudFlareVideoData(
    @SerializedName("CloudFlareVideo") val CloudFlareVideo : List<String>,
    @SerializedName("created") val created : String,
    @SerializedName("duration") val duration : Int,
    @SerializedName("input") val input : CloudFlareInput,
    @SerializedName("maxDurationSeconds") val maxDurationSeconds : Int,
    @SerializedName("modified") val modified : String,
    @SerializedName("uploadExpiry") val uploadExpiry : String,
    @SerializedName("playback") val playback : CloudFlarePlayback,
    @SerializedName("preview") val preview : String,
    @SerializedName("readyToStream") val readyToStream : Boolean,
    @SerializedName("requireSignedURLs") val requireSignedURLs : Boolean,
    @SerializedName("size") val size : Int,
    @SerializedName("status") val status : CloudFlareState,
    @SerializedName("thumbnail") val thumbnail : String,
    @SerializedName("thumbnailTimestampPct") val thumbnailTimestampPct : Int,
    @SerializedName("uid") val uid : String,
    @SerializedName("creator") val creator : String,
    @SerializedName("liveInput") val liveInput : String,
    @SerializedName("uploaded") val uploaded : String,
    @SerializedName("watermark") val watermark : String?,
    @SerializedName("nft") val nft : CloudFlareSNft,
)

data class CloudFlareInput(
    @SerializedName("height") val height : Int,
    @SerializedName("width") val width : Int,
)

data class CloudFlarePlayback(
    @SerializedName("hls") val hls : String,
    @SerializedName("dash") val dash : String
)

data class CloudFlareState(
    @SerializedName("state") val state : String,
    @SerializedName("pctComplete") val pctComplete : Int,
    @SerializedName("errorReasonCode") val errorReasonCode : String,
    @SerializedName("errorReasonText") val errorReasonText : String
)

data class CloudFlareSNft(
    @SerializedName("contract") val contract : String,
    @SerializedName("token") val token : Int,
)