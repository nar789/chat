package com.rndeep.fns_fantoo.data.remote.model.editor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attach(
    @field:SerializedName("attachType") val attachType: String,
    @field:SerializedName("id") val id: String
) : Parcelable
