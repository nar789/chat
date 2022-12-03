package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.AlarmConfigDto
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubAlarmStateDto
import com.rndeep.fns_fantoo.data.remote.model.community.LikeDislikeResult
import com.rndeep.fns_fantoo.data.remote.model.community.MyCommentItemDataObj
import com.rndeep.fns_fantoo.data.remote.model.community.PostBookmarkDataObj
import com.rndeep.fns_fantoo.data.remote.model.community.PostMyPostDataObj
import com.rndeep.fns_fantoo.utils.ConstVariable
import retrofit2.http.*

interface PostService {
    //커뮤니티 좋아요,싫어요
    @POST("/community/{likeType}/{targetType}/target-id/{targetId}")
    suspend fun callCommunityPostLikeDisLike(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("likeType")likeType : String,
        @Path("targetType")targetType :String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    ) : LikeDislikeResult
    //클럽 좋아요, 싫어요 (현재 커뮤니티 용)
    @POST("/community/{likeType}/{targetType}/target-id/{targetId}")
    suspend fun callClubPostLikeDisLike(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("likeType")likeType : String,
        @Path("targetType")targetType :String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    ) : LikeDislikeResult

    //커뮤니티 좋아요,싫어요 취소
    @HTTP(method = "DELETE", path = "/community/like/{targetType}/target-id/{targetId}", hasBody = true)
    suspend fun cancelCommunityPostLikeDisLike(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType: String,
        @Path("targetId")targetId :String,
        @Body hashBody :HashMap<String,Any>
    ) :LikeDislikeResult

    //클럽 좋아요,싫어요 취소
    @HTTP(method = "DELETE", path = "/community/like/{targetType}/target-id/{targetId}", hasBody = true)
    suspend fun cancelClubPostLikeDisLike(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType: String,
        @Path("targetId")targetId :String,
        @Body hashBody :HashMap<String,Any>
    ) :LikeDislikeResult

    //커뮤니티 아너
    @POST("/community/honor/{targetType}/target-id/{targetId}")
    suspend fun callCommunityPostHonor(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType :String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    ) : BaseResponse
    //클럽 아너
    @POST("/community/honor/{targetType}/target-id/{targetId}")
    suspend fun callClubPostHonor(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType :String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    ) : BaseResponse

    //커뮤니티 아너 취소
    @HTTP(method = "DELETE", path = "/community/honor/{targetType}/target-id/{targetId}", hasBody = true)
    suspend fun cancelCommunityPostHonor(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType: String,
        @Path("targetId")targetId :String,
        @Body hashBody :HashMap<String,Any>
    ) :BaseResponse

    //클럽 아너 취소
    @HTTP(method = "DELETE", path = "/community/honor/{targetType}/target-id/{targetId}", hasBody = true)
    suspend fun cancelClubPostHonor(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType: String,
        @Path("targetId")targetId :String,
        @Body hashBody :HashMap<String,Any>
    ) :BaseResponse

    //커뮤니티 게시글 차단
    @POST("/community/block/piece/{targetType}/target-id/{targetId}")
    suspend fun blockCommunityPiece(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType : String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    )
    //클럽 게시글 차단
    @POST("/community/block/piece/{targetType}/target-id/{targetId}")
    suspend fun blockClubPiece(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType : String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    )

    //커뮤니티 게시글 차단 해제
    @HTTP(method = "DELETE", path = "/community/block/piece/{targetType}/target-id/{targetId}", hasBody = true)
    suspend fun unblockCommunityPiece(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType : String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    )
    //클럽 게시글 차단 해제
    @HTTP(method = "DELETE", path = "/community/block/piece/{targetType}/target-id/{targetId}", hasBody = true)
    suspend fun unblockClubPiece(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("targetType")targetType : String,
        @Path("targetId")targetId : String,
        @Body hashBody :HashMap<String,Any>
    )

    //커뮤니티 회원 차단
    @POST("/community/block/user")
    suspend fun blockAccountCommunityPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Body hashBody :HashMap<String,Any>
    )
    //클럽 회원 차단
    @POST("/community/block/user")
    suspend fun blockAccountClubPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Body hashBody :HashMap<String,Any>
    )

    //커뮤니티 회원 차단 해제
    @HTTP(method = "DELETE", path = "/community/block/user", hasBody = true)
    suspend fun unblockAccountCommunityPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Body hashBody :HashMap<String,Any>
    )

    //클럽 회원 차단 해제
    @HTTP(method = "DELETE", path = "/community/block/user", hasBody = true)
    suspend fun unblockAccountClubPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Body hashBody :HashMap<String,Any>
    )

    //커뮤니티 게시글 저장
    @POST("/community/my/bookmark/{postId}")
    suspend fun callBookmarkPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("postId")postId :String,
        @Body body : HashMap<String,Any>
    )

    //커뮤니티 게시글 저장 취소
    @HTTP(method = "DELETE", path = "/community/my/bookmark/{postId}", hasBody = true)
    suspend fun callUnBookmarkPost(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Path("postId")postId :String,
        @Body body : HashMap<String,Any>
    )

    //저장 게시글 조회
    @GET("/community/my/bookmark")
    suspend fun getBookmarkPostList(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Query("integUid") integUid :String,
        @Query("nextId") nextId :Int,
        @Query("size") size :String
    ) : PostBookmarkDataObj

    //내 작성 게시글
    @GET("/community/my/post")
    suspend fun getMyPostList(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Query("integUid") integUid :String,
        @Query("nextId") nextId :Int,
        @Query("size") size :String
    ): PostMyPostDataObj

    //내 작성 댓글
    @GET("/community/my/reply")
    suspend fun getMyReply(
        @Header(ConstVariable.KEY_ACCESS_TOKEN)accessToken: String,
        @Query("integUid") integUid :String,
        @Query("nextId") nextId :Int,
        @Query("size") size :String
    ): MyCommentItemDataObj


    @GET("/user/alim")
    suspend fun getMyCommunityAlimState(
        @Query(ConstVariable.KEY_UID)uId :String
    ) : AlarmConfigDto

    @PATCH("/user/alim")
    suspend fun patchMyCommunityAlimState(
        @Body hashBody : HashMap<String,Any>
    ) : ClubAlarmStateDto
}