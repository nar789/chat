package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MyWriteCountResponse(
    @field:SerializedName("postCnt") val postCnt: Int,
    @field:SerializedName("replyCnt") val replyCnt: Int,
    @field:SerializedName("bookmarkCnt") val bookmarkCnt: Int
)
