package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.*
import com.rndeep.fns_fantoo.data.remote.model.ClubBasicInfo
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.ComposeComment
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.ComposeCommentReply
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.ReportMessage
import retrofit2.http.*

interface FantooClubService {

    @GET("club/{clubId}/basic")
    suspend fun getClubBasicInfo(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String
    ): ClubBasicInfo

    @GET("club/{clubId}/follow")
    suspend fun fetchFantooClubFollow(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String
    ): FantooClubIsFollowResponse

    @PATCH("club/{clubId}/follow")
    suspend fun setFantooClubFollow(
        @Path("clubId") clubId: String,
        @Body integUid: IntegUid
    ): FantooClubFollowResponse

    @GET("club/{clubId}/board/{categoryCode}/post")
    suspend fun fetchFantooClubPosts(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Query("integUid") integUid: String?,
        @Query("nextId") nextId: String?,
        @Query("size") size: String
    ): FantooClubPostsResponse

    @PATCH("club/{clubId}/{likeType}/{categoryCode}/post/{postId}")
    suspend fun setFantooClubPostLikeAndDislike(
        @Path("clubId") clubId: String,
        @Path("likeType") likeType: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Body integUid: IntegUid
    ): FantooClubLikeResponse

    @PATCH("club/{clubId}/{likeType}/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun setFantooClubCommentLikeAndDislike(
        @Path("clubId") clubId: String,
        @Path("likeType") likeType: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Body integUid: IntegUid
    ): FantooClubLikeResponse

    @PATCH("club/{clubId}/{likeType}/{categoryCode}/post/{postId}/reply/{parentReplyId}/replys/{replyId}")
    suspend fun setFantooClubCommentReplyLikeAndDislike(
        @Path("clubId") clubId: String,
        @Path("likeType") likeType: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("parentReplyId") parentReplyId: String,
        @Path("replyId") replyId: String,
        @Body integUid: IntegUid
    ): FantooClubLikeResponse

    @GET("club/{clubId}/category/{categoryCode}")
    suspend fun fetchFantooClubCategories(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Query("integUid") integUid: String?
    ): FantooClubCategoryResponse

    @GET("club/{clubId}/board/{categoryCode}/post/{postId}/reply")
    suspend fun fetchFantooClubComments(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Query("integUid") integUid: String?
    ): FantooClubCommentsResponse

    @GET("club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}/replys")
    suspend fun fetchFantooClubCommentReplies(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Query("integUid") integUid: String?
    ): FantooClubCommentsResponse

    @POST("club/{clubId}/board/{categoryCode}/post/{postId}/reply")
    suspend fun composeFantooClubComment(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Body ComposeComment: ComposeComment
    )

    @POST("club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}/replys")
    suspend fun composeFantooClubCommentReply(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Body ComposeCommentReply: ComposeCommentReply
    )

    @PATCH("club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun modifyFantooClubComment(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Body ComposeComment: ComposeComment
    )

    @PATCH("club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}/replys/{rereplyId}")
    suspend fun modifyFantooClubCommentReply(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Path("rereplyId") rereplyId: String,
        @Body ComposeCommentReply: ComposeCommentReply
    )

    @HTTP(
        method = "DELETE",
        path = "club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}",
        hasBody = true
    )
    suspend fun deleteFantooClubComment(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Body integUid: IntegUid
    )

    @HTTP(
        method = "DELETE",
        path = "club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}/replys/{rereplyId}",
        hasBody = true
    )
    suspend fun deleteFantooClubCommentReply(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Path("rereplyId") rereplyId: String,
        @Body integUid: IntegUid
    )

    @PATCH("club/{clubId}/report/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun reportFantooClubComment(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Body reportMessage: ReportMessage
    )

    @PATCH("club/{clubId}/report/{categoryCode}/post/{postId}/reply/{replyId}/replys/{rereplyId}")
    suspend fun reportFantooClubCommentReply(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Path("rereplyId") rereplyId: String,
        @Body reportMessage: ReportMessage
    )

    @PATCH("club/{clubId}/block/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun blockFantooClubComment(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Body integUid: IntegUid
    )

    @PATCH("club/{clubId}/block/{categoryCode}/post/{postId}/reply/{replyId}/replys/{rereplyId}")
    suspend fun blockFantooClubCommentReply(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Path("rereplyId") rereplyId: String,
        @Body integUid: IntegUid
    )

    @GET("club/{clubId}/post/search")
    suspend fun searchFantooClubPosts(
        @Path("clubId") clubId: String,
        @Query("keyword") keyword: String
    ): FantooClubPostsResponse

    @GET("report/message")
    suspend fun fetchReportMessages() : ReportMessageResponse
}