package com.rndeep.fns_fantoo.data.remote.model.userinfo

import com.google.gson.annotations.SerializedName

data class Fanit(
    @field:SerializedName("image") val image: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("fanIt") val fanIt: Int
)

data class Kdg(
    @field:SerializedName("image") val image: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("kdg") val kdg: Int
)

data class Honor(
    @field:SerializedName("image") val image: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("honor") val honor: Int
)
