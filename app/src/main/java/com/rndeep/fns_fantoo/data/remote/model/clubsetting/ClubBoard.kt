package com.rndeep.fns_fantoo.data.remote.model.clubsetting

import com.google.gson.annotations.SerializedName

data class ClubBoard(
    @field:SerializedName("clubBoardId") val clubBoardId: Long,
    @field:SerializedName("boardType") val boardType: Int,
    @field:SerializedName("clubManageId") val clubManageId: Long,
    @field:SerializedName("boardTitle") val boardTitle: String,
    @field:SerializedName("subject") val subject: String,
    @field:SerializedName("isView") val isView: Int,
    @field:SerializedName("clubArchiveType") val clubArchiveType: String,
    @field:SerializedName("boardOrderNum") val boardOrderNum: Int
)
