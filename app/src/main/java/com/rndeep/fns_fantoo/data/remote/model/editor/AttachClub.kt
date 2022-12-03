package com.rndeep.fns_fantoo.data.remote.model.editor

import com.google.gson.annotations.SerializedName

data class AttachClub(
    @field:SerializedName("attach") val attach: String,
    @field:SerializedName("attachType") val attachType: Int,
    @field:SerializedName("attachId") val attachId: Int?,
)
