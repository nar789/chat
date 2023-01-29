package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ConversationBlockResponse(
    @field:SerializedName("blockYn") val blockYn: Boolean
)
