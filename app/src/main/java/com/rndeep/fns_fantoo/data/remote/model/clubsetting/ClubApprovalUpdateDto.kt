package com.rndeep.fns_fantoo.data.remote.model.clubsetting

data class ClubApprovalUpdateDto(
    val clubManageId:Int,
    val clubMemberManageId:List<String>,
    val integUid:String
    )