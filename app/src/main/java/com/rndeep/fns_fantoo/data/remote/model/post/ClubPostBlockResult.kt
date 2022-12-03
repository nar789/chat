package com.rndeep.fns_fantoo.data.remote.model.post

import androidx.navigation.NavDirections
import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberBlockInfoDto

data class ClubPostBlockResult(
    @SerializedName("blockYn") val blockYn :Boolean,
)

data class ClubCommentBlockResult(
    @SerializedName("blockYn") val blockYn :Boolean,
)

data class ClubPostBlockResultResponse(
    val code: String,
    val msg: String,
    val dataObj: ClubPostBlockResult?,
    val navDirections: NavDirections?,
    val errorData: ErrorData?
)

data class ClubCommentBlockResultResponse(
    val code: String,
    val msg: String,
    val dataObj: ClubCommentBlockResult?,
    val navDirections: NavDirections?,
    val errorData: ErrorData?
)