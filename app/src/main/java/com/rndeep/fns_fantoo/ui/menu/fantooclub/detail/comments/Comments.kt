package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.comments

data class Comments(
    val commentsId: Long,
    val parentId: Long,
    val clubId: Long,
    val memberId: Long,
    val createdAt: String,
    val langCode: String,
    val content: String,
    val nickname: String,
    val attach: List<Attach>,
    val replyCount: Int,
    val likeCount: Int,
    val dislikeCount: Int,
    val level: Int,
    val status: Int,
    val depth: Int
)

data class Attach(
    val type: Int,
    val id: Long,
    val url: String
)