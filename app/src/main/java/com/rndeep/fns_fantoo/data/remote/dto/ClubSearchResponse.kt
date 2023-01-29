package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.club.ClubSearchItem

data class ClubSearchResponse(
    @SerializedName("clubList")val clubList: List<ClubSearchItem>,
    @SerializedName("listSize")val listSize: Int,
    @SerializedName("nextId")val nextId: Int,
    @SerializedName("size")val size: Int,
)
