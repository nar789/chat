package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.model.ClubNameCheck

data class ClubCheckNameResponse(
    val code: String,
    val msg: String,
    val dataObj: ClubNameCheck?,
    val errorData: ErrorData?
)
