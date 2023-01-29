package com.rndeep.fns_fantoo.data.remote.model.editor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Hashtag(
    @field:SerializedName("tag") val tag: String
) : Parcelable
