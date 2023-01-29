package com.rndeep.fns_fantoo.ui.community.postdetail.data

import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.*

sealed class DetailPostItem {

    data class CommunityPostDetail(
        val type : String,
        val item : BoardPostDetailData,
        val attachItems : List<DetailAttachList>,
        val hashTagItems : List<BoardPostDetailHashTag>,
        val openGraphItems : List<PostDetailOpenGraphTag>?
    ) : DetailPostItem()

    data class ClubPostDetail(
        val type : String,
        val item : ClubPostDetailData,
    ) : DetailPostItem()

    data class NoticePostDetail(
        val type : String,
        val item : CommunityNoticeData,
        val attachItems : List<DetailAttachList>?,
    ) : DetailPostItem()

    data class CommunityPostComment(
        val type : String,
        val item : CommunityReplyData
    ) : DetailPostItem()

    data class ClubPostComment(
        val type : String,
        val item : ClubReplyData
    ) : DetailPostItem()

}