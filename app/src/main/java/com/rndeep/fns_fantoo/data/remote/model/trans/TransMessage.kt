package com.rndeep.fns_fantoo.data.remote.model.trans

import com.google.gson.annotations.SerializedName

data class TransMessage(
    @SerializedName("origin")val origin:String,
    @SerializedName("text")val text:String,
    @SerializedName("isTranslated")val isTranslated:Boolean,
)
