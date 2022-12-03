package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class CloudFlareImage (
    @SerializedName("result")val result : CloudFlareImageData,
    @SerializedName("success")val success : Boolean,
    @SerializedName("errors")val errors : List<Any>,
    @SerializedName("messages")val messages : List<Any>,
)

data class CloudFlareImageData(
    @SerializedName("id") val id : String,
    @SerializedName("filename") val filename : String,
    @SerializedName("uploaded") val uploaded : String,
    @SerializedName("requireSignedURLs") val requireSignedURLs : Boolean,
    @SerializedName("variants") val variants : List<String>,
)