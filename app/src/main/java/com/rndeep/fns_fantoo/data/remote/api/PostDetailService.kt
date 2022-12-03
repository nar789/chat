package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.ReportMessageResponse
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyDataObj
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyDataObj
import com.rndeep.fns_fantoo.data.remote.model.community.BoardPostDetailResponse
import com.rndeep.fns_fantoo.data.remote.model.community.ClubPostDetailData
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeDetailResponse
import com.rndeep.fns_fantoo.data.remote.model.community.LikeDislikeResult
import com.rndeep.fns_fantoo.data.remote.model.post.ClubBookmarkResult
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostBlockResult
import com.rndeep.fns_fantoo.utils.ConstVariable
import retrofit2.http.*

interface PostDetailService {

    //신고 리스트 조회[공통]
    @GET("/report/message")
    suspend fun getReportMessages() : ReportMessageResponse

    //게시판 정보
    @GET("/dummy/post/info")
    suspend fun getPostDetailInfo(): BaseResponse

    //게시글 상세(커뮤니티)
    //게시글 상세 내용 (회원용)
    @GET("/community/{code}/post/{postId}")
    suspend fun getCommunityPostDetail(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("code") code: String,
        @Path("postId") postId: String,
        @Query("integUid") uId: String
    ): BoardPostDetailResponse

    //게시글 상세 내용 (게스트용)
    @GET("/community/guest/{code}/post/{postId}")
    suspend fun getCommunityPostDetail(
        @Path("code") code: String,
        @Path("postId") postId: String
    ): BoardPostDetailResponse

    //게시글 상세(클럽)
    //게시글 상세 내용 (회원용)
    @GET("/club/{clubId}/board/{categoryCode}/post/{postId}")
    suspend fun getClubPostDetail(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("categoryCode") categoryCode: String,
        @Path("clubId") clubId: String,
        @Path("postId") postId: Int,
        @Query("integUid") uId: String
    ): ClubPostDetailData

    //게시글 상세 내용 (게스트용)
    @GET("/club/{clubId}/guest/board/{categoryCode}/post/{postId}")
    suspend fun getClubPostDetail(
        @Path("categoryCode") categoryCode: String,
        @Path("clubId") clubId: String,
        @Path("postId") postId: Int,
    ): ClubPostDetailData
    //게시글 북마크 여부[클럽]
    @GET("/club/{clubId}/bookmark/{categoryCode}/post/{postId}")
    suspend fun getClubPostBookMarkYn(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Query(ConstVariable.KEY_UID)uId: String
    ) : ClubBookmarkResult
    //게시글 북마크 변경
    @PATCH("/club/{clubId}/bookmark/{categoryCode}/post/{postId}")
    suspend fun patchClubPostBookMarkYn(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Body hashBody: HashMap<String, Any>
    ) : ClubBookmarkResult

    //게시글 상세(챌린지)
    @GET("/club/challenge/post/{postId}")
    suspend fun getChallengePostDetail(
        @Path("postId") postId: Int,
        @Query(ConstVariable.KEY_UID) uId: String
    ): ClubPostDetailData

    //게시글 상세(공지)
    //일반 공지 상세 게시글
    @GET("/community/notice/{postId}")
    suspend fun getNoticeDetailItem(@Path("postId") postId: Int): CommunityNoticeDetailResponse


    //특정 게시판 공지 상세 게시글
    @GET("/community/{code}/notice/{postId}")
    suspend fun getNoticeDetailItem(
        @Path("code") code: String,
        @Path("postId") postId: Int
    ): CommunityNoticeDetailResponse

    //북마크
    @POST("/community/my/bookmark/{postId}")
    suspend fun callRegisterBookmark(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("postId") postId: String,
        @Body body: HashMap<String, Any>
    )

    //북마크 제거
    @HTTP(method = "DELETE", hasBody = true, path = "/community/my/bookmark/{postId}")
    suspend fun callUnRegisterBookmark(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("postId") postId: String,
        @Body body: HashMap<String, Any>
    )

    //번역
    @GET("/temp/translate/content")
    suspend fun callTranslateContent()

    //커뮤니티 게시글,댓글 좋아요 싫어요
    @POST("/community/{likeType}/{targetType}/target-id/{targetId}")
    suspend fun clickPostCommentLike(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("likeType") likeType: String,
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: String,
        @Body hashBody : HashMap<String,Any>
    ): LikeDislikeResult

    //좋아요 싫어요 취소
    @HTTP(method="DELETE",path = "/community/like/{targetType}/target-id/{targetId}" ,hasBody = true)
    suspend fun cancelPostLike(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: String,
        @Body hashBody : HashMap<String,Any>
    ): LikeDislikeResult

    // 아너 클릭
    @GET("/post/honor/click")
    suspend fun clickPostHonor(): BaseResponse

    @POST("/community/block/piece/{targetType}/target-id/{targetId}")
    suspend fun blockPiecePost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType : String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    )

    @HTTP(method = "DELETE", path = "/community/block/piece/{targetType}/target-id/{targetId}", hasBody = true)
    suspend fun unblockPiecePost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType : String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    )

    @POST("/community/block/user")
    suspend fun blockUserPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Body hashBody :HashMap<String,Any>
    )

    @HTTP(method = "DELETE", path = "/community/block/user", hasBody = true)
    suspend fun unblockUserPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Body hashBody :HashMap<String,Any>
    )

    @HTTP(method = "DELETE" , path = "/community/{code}/post/{postId}", hasBody = true)
    suspend fun deleteMyPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("code") code : String,
        @Path("postId") postId: String,
        @Body hashBody : HashMap<String,Any>
    )

    //게시글 댓글 신고
    @POST("/community/report/{targetId}")
    suspend fun reportCommunityPostComment(
        @Path("targetId")targetId: Int,
        @Query("targetType") targetType: String,
        @Body hashBody : HashMap<String,Any>
    )

    //댓글 리스트 받기(커뮤니티)
    @GET("/community/guest/{postId}/reply")
    suspend fun getCommunityCommentListInfo(
        @Path("postId") postId: String,
        @Query("nextId") nextId: Int?,
        @Query("size") size: Int
    ): CommunityReplyDataObj

    @GET("/community/{postId}/reply")
    suspend fun getCommunityCommentListInfo(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("postId") postId: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: Int?,
        @Query("size") size: Int
    ): CommunityReplyDataObj

    //댓글 리스트 받기(클럽)
    @GET("/club/{clubId}/guest/board/{categoryCode}/post/{postId}/reply")
    suspend fun getClubCommentListInfo(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: Int,
        @Query("nextId") nextId: String?,
        @Query("size") size: Int
    ): ClubReplyDataObj

    @GET("/club/{clubId}/board/{categoryCode}/post/{postId}/reply")
    suspend fun getClubCommentListInfo(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: Int,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: String?,
        @Query("size") size: Int
    ): ClubReplyDataObj

    @GET("/club/{clubId}/guest/board/{categoryCode}/post/{postId}/reply/{replyId}/replys")
    suspend fun fetchClubCommentReplys(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: Int,
        @Path("replyId") replyId: Int,
        @Query("nextId") nextId: String?,
        @Query("size") size :Int
    ): ClubReplyDataObj

    @GET("/club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}/replys")
    suspend fun fetchClubCommentReplys(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: Int,
        @Path("replyId") replyId: Int,
        @Query(ConstVariable.KEY_UID)uId :String?,
        @Query("nextId") nextId: String?,
        @Query("size") size :Int
    ): ClubReplyDataObj

    //클럽 챌린지 댓글
    @GET("/club/challenge/post/{postId}/reply")
    suspend fun fetchClubChallengeComment(
        @Path("postId")postId: Int,
        @Query(ConstVariable.KEY_UID) uId: String,
        @Query("nextId")nextId: String?,
        @Query("size")size :Int
    ) :ClubReplyDataObj

    //커뮤니티 댓글 상세
    @GET("/community/guest/{postId}/reply/{replyId}")
    suspend fun getCommentDetailInfo(
        @Path("postId") postId: Int,
        @Path("replyId") replyId: Int,
        @Query("nextId") nextId: String?,
        @Query("size") size: Int
    ): CommunityReplyDataObj

    @GET("/community/{postId}/reply/{replyId}")
    suspend fun getCommentDetailInfo(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("postId") postId: Int,
        @Path("replyId") replyId: Int,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: String?,
        @Query("size") size: Int
    ): CommunityReplyDataObj

    //댓글 쓰기[클럽]
    @POST("/club/{clubId}/board/{categoryCode}/post/{postId}/reply")
    suspend fun registerClubComment(
        @Path("clubId")clubId : String,
        @Path("categoryCode")categoryCode : String,
        @Path("postId")postId : Int,
        @Body commentBody : HashMap<String,Any>
    )

    //댓글 수정[클럽]
    @PATCH("/club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun modifyClubComment(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Path("replyId")replyId: Int,
        @Body modifyBody : HashMap<String,Any>
    )
    //댓글 삭제[클럽]
    @HTTP(method = "DELETE", path = "/club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}", hasBody = true)
    suspend fun deleteClubPostComment(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Path("replyId")replyId: Int,
        @Body hashbody : HashMap<String,Any>
    )
    //대댓글 삭제[클럽]
    @HTTP(method = "DELETE", path = "/club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}/replys/{rereplyId}", hasBody = true)
    suspend fun deleteClubPostCommentReply(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Path("replyId")replyId: Int,
        @Path("rereplyId") pReplyId:Int,
        @Body hashbody : HashMap<String,Any>
    )
    //댓글 좋아요, 싫어요
    @POST("/community/{likeType}/{targetType}/target-id/{targetId}")
    suspend fun clickCommentLike(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("likeType")likeType: String,
        @Path("targetType")targetType: String,
        @Path("targetId")targetId: String,
        @Body hashBody :HashMap<String,String>
    ): LikeDislikeResult

    //댓글 신고[커뮤니티]
    @GET("/comment/report")
    suspend fun reportComment(
    ): BaseResponse

    //댓글 입력[커뮤니티]
    @POST("/community/{postId}/reply")
    suspend fun sendPostComment(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("postId") postId: String,
        @Body bodyData: HashMap<String, Any>
    )
    //댓글 삭제[커뮤니티]
    @HTTP(method = "DELETE", path = "/community/{postId}/reply/{replyId}", hasBody = true)
    suspend fun deleteCommunityPostComment(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Body hashBody: HashMap<String, String>
    )

    //댓글 수정[커뮤니티]
    @HTTP(method = "PUT", path = "/community/{postId}/reply/{replyId}", hasBody = true)
    suspend fun modifyPostComment(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("postId") postId: String,
        @Path("replyId") replyId: String,
        @Body hashBody: HashMap<String, Any>
    )

    //대댓글 쓰기[커뮤나티]
    @POST("/community/{postId}/target-reply/{targetReplyId}")
    suspend fun sendCommentReply(
        @Header(ConstVariable.KEY_ACCESS_TOKEN) accessToken: String,
        @Path("postId") postId: String,
        @Path("targetReplyId") targetReply: String,
        @Body hashBody: HashMap<String, Any>
    )
    //대댓글쓸기[클럽]
    @POST("/club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}/replys")
    suspend fun sendClubCommentReply(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: Int,
        @Path("replyId") replyId: Int,
        @Body hashBody: HashMap<String, Any>
    )
    //대댓글 수정[클럽]
    @PATCH("/club/{clubId}/board/{categoryCode}/post/{postId}/reply/{replyId}/replys/{rereplyId}")
    suspend fun modifyClubCommentReply(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: Int,
        @Path("replyId") replyId: Int,
        @Path("rereplyId") reReplyId: Int,
        @Body hashBody: HashMap<String, Any>
    )
    //댓글 차단 및 해제[클럽]
    @PATCH("/club/{clubId}/block/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun patchBlockClubComment(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: Int,
        @Path("replyId") replyId: Int,
        @Body hashBody : HashMap<String,Any>
    )

    //댓글 신고[클럽]
    @PATCH("/club/{clubId}/report/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun reportClubComment(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Path("replyId")replyId: Int,
        @Body hashBody :HashMap<String,Any>
    )

    //대댓글 신고[클럽]
    @PATCH("/club/{clubId}/report/{categoryCode}/post/{postId}/reply/{parentReplyId}/replys/{replyId}")
    suspend fun reportClubCommentReply(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Path("parentReplyId")parentReplyId: Int,
        @Path("replyId")replyId: Int,
        @Body hashBody :HashMap<String,Any>
    )

    //대댓글 차단 및 해제[클럽]
    @PATCH("/club/{clubId}/block/{categoryCode}/post/{postId}/reply/{parentReplyId}/replys/{replyId}")
    suspend fun patchBlockClubCommentReply(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId: Int,
        @Path("parentReplyId") parentReplyId: Int,
        @Path("replyId") replyId: Int,
        @Body hashBody : HashMap<String,Any>
    )
    //클럽 멤버 차단
    @PATCH("/club/{clubId}/block/member/{memberId}")
    suspend fun patchBlockClubMember(
        @Path("clubId")clubId: String,
        @Path("memberId")memberId :Int,
        @Body hashBody : HashMap<String,Any>
    )

    //게시글 차단 및 해제 [클럽]
    @PATCH("/club/{clubId}/block/{categoryCode}/post/{postId}")
    suspend fun patchBlockClubPost(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Body hashBody : HashMap<String,Any>
    ) :ClubPostBlockResult

    //게시글 삭제
    @HTTP(method = "DELETE" , path = "/club/{clubId}/board/{categoryCode}/post/{postId}",hasBody = true)
    suspend fun patchDeleteClubPost(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Body hashBody : HashMap<String,Any>
    )
    //게시글 신고
    @PATCH("/club/{clubId}/report/{categoryCode}/post/{postId}")
    suspend fun reportClubPost(
        @Path("clubId")clubId: String,
        @Path("categoryCode")categoryCode: String,
        @Path("postId")postId: Int,
        @Body hashBody : HashMap<String,Any>
    )

}