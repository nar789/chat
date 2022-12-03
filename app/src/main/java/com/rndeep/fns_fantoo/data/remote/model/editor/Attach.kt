package com.rndeep.fns_fantoo.data.remote.model.editor

import com.google.gson.annotations.SerializedName

data class Attach(
    @field:SerializedName("attachType") val attachType: String,
    @field:SerializedName("id") val id: String
)
