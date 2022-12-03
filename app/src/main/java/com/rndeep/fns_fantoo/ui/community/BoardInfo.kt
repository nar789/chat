package com.rndeep.fns_fantoo.ui.community

import java.io.Serializable

data class BoardInfo(
    var boardName :String,
    var boardId :String?,
    var boardType :String,
) : Serializable
