package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail

import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubComment

data class FantooClubComposeComment(
    val fantooClubComment: FantooClubComment? = null,
    var commentMode: ComposeCommentMode = ComposeCommentMode.NEW
)

enum class ComposeCommentMode {
    NEW, EDIT
}
