package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.*
import com.rndeep.fns_fantoo.data.remote.model.*
import com.rndeep.fns_fantoo.data.remote.model.club.ClubLoginResult
import com.rndeep.fns_fantoo.data.remote.model.club.ClubTopPostResponse
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.*
import com.rndeep.fns_fantoo.data.remote.model.post.ClubCommentBlockResult
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostBlockResult
import com.rndeep.fns_fantoo.utils.ConstVariable
import retrofit2.http.*

interface ClubService {

    @GET("/club/main/popular/post/top10")
    suspend fun fetchTop10PostList(
        @Query(ConstVariable.KEY_UID) integUid: String,
    ) : ClubTopPostResponse

    //인기클럽 추천 카테고리 리스트
    @GET("/club/main/popular/club/category/list")
    suspend fun fetchHotClubRecommendCategoryList(
        @Query(ConstVariable.KEY_UID) integUid: String,
    ) : ClubRecommendCategoryResponse

    //인기클럽 추천 카테고리 리스트(비로그인)
    @GET("/club/main/guest/popular/club/category/list")
    suspend fun fetchGuestHotClubRecommendCategoryList(
    ) : ClubRecommendCategoryResponse

    //인기클럽 신규 10
    @GET("/club/main/newly/club/top10")
    suspend fun fetchNewRecommendClubs(
        @Query(ConstVariable.KEY_UID) integUid: String,
    ): RecommendationClubResponse

    //인기클럽 신규 10(비로그인)
    @GET("/club/main/guest/newly/club/top10")
    suspend fun fetchGuestNewRecommendClubs(
    ): RecommendationClubResponse

    //인기클럽 추천 카테고리 10
    @GET("/club/main/popular/club/category/top10")
    suspend fun fetchPopularRecommendCategoryClubs(
        @Query(ConstVariable.KEY_UID) integUid: String,
        @Query("categoryCode") categoryCode: String?
    ): RecommendationClubResponse

    //인기클럽 추천 카테고리 10(비로그인)
    @GET("/club/main/guest/popular/club/category/top10")
    suspend fun fetchGuestPopularRecommendCategoryClubs(
        @Query("categoryCode") categoryCode: String?
    ): RecommendationClubResponse

    //인기클럽 추천
    @GET("/club/main/popular/club/top10")
    suspend fun fetchPopularRecommendClubs(
        @Query(ConstVariable.KEY_UID) integUid: String,
    ): RecommendationClubResponse

    //인기클럽 추천(비로그인)
    @GET("/club/main/guest/popular/club/top10")
    suspend fun fetchGuestPopularRecommendClubs(
    ): RecommendationClubResponse

    //인기클럽 100
    @GET("/club/main/popular/club/top100")
    suspend fun fetchPopularRecommendClub100(
        @Query(ConstVariable.KEY_UID) integUid: String,
    ):ClubSearchResponse

    //인기클럽 100(비로그인)
    @GET("/club/main/guest/popular/club/top100")
    suspend fun fetchGuestPopularRecommendClub100(
    ):ClubSearchResponse

    //클럽 공지
    @GET("/club/{clubId}/board/notice/post")
    suspend fun fetchClubNotice(
        @Path("clubId") clubId: String,
        @Query(ConstVariable.KEY_UID) uId: String,
        @Query("nextId") nextId: Int?,
        @Query("size") size: Int
    ): ClubNoticeResponse

    @GET("/club/{clubId}/guest/board/notice/post")
    suspend fun fetchClubNoticeForGuest(
        @Path("clubId") clubId: String,
        @Query("nextId") nextId: Int?,
        @Query("size") size: Int
    ): ClubNoticeResponse

    //클럽 기본정보
    @GET("/club/{clubId}/basic")
    suspend fun getClubBasicInfo(
        @Path("clubId") clubId: String,
        @Query(ConstVariable.KEY_UID) integUid: String
    ): ClubBasicInfo

    //클럽 접속 로그인
    @PATCH("/club/{clubId}/login")
    suspend fun loginClub(
        @Path("clubId") clubId: String,
        @Body body: HashMap<String, Any>
    ): ClubLoginResult

    //클럽 좋아요
    @GET("/club/{clubId}/favorite")
    suspend fun getClubFavoriteInfo(
        @Path("clubId") clubId: String,
        @Query(ConstVariable.KEY_UID) integUid: String
    ): FavoriteResult

    @PATCH("/club/{clubId}/favorite")
    suspend fun patchClubFavoriteInfo(
        @Path("clubId") clubId: String,
        @Body body: HashMap<String, Any>
    ): FavoriteResult

    //클럽 검색
    @GET("/club/search")
    suspend fun fetchSearchClub(
        @Query("keyword")keyword: String,
        @Query("nextId")nextId: Int?,
        @Query("size")size: Int
    ) : ClubSearchResponse

    @GET("/club/{clubId}/post/search")
    suspend fun searchClubPost(
        @Path("clubId")clubId: String,
        @Query("keyword")keyword: String,
        @Query("nextId")nextId: Int?,
        @Query("size") size :Int
    ) : ClubPagePostResponse

    //클럽 카테고리 리스트
    @GET("/club/{clubId}/category")
    suspend fun getClubCategoryList(@Path("clubId")clubId: String,@Query(ConstVariable.KEY_UID)integUid: String,@Query("sort")sortType :String) : ClubCategoryResponse

    @GET("club/my")
    suspend fun fetchMyClubs(
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: String?,
        @Query("size") size: String?,
        @Query("sort") sort: String?,
    ): MyClubResponse

    @GET("/club/{clubId}/category")
    suspend fun fetchClubOneDepthCategories(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String
    ): ClubCategoryResponse

    @GET("/club/{clubId}/category/{categoryCode}")
    suspend fun fetchClubTwoDepthCategories(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Query("integUid") integUid: String?
    ): ClubSubCategoryResponse

    @POST("/club/{clubId}/board/{categoryCode}/post")
    suspend fun composeClubPost(
        @Header("access_token") accessToken: String,
        @Body composeClubPost: ComposeClubPost,
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String
    )

    @GET("/club/my/basic")
    suspend fun getMyClubList(
        @Query(ConstVariable.KEY_UID) integUid: String,
        @Query("nextId") nextId: String?,
        @Query("size") size: Int,
    ): MyClubResponse


    //클럽 페이지 게시글
    @GET("club/{clubId}/board/{categoryCode}/post")
    suspend fun getClubCategoryPost(
        @Path("categoryCode") categoryCode: String,
        @Path("clubId") clubId: String,
        @Query(ConstVariable.KEY_UID) integUid: String,
        @Query("nextId") nextId: String?,
        @Query("size") size: String,
        @Query("sort") sort: String,
    ): ClubPagePostResponse

    @GET("club/{clubId}/guest/board/{categoryCode}/post")
    suspend fun getClubCategoryPostForGuest(
        @Path("categoryCode") categoryCode: String,
        @Path("clubId") clubId: String,
        @Query("nextId") nextId: String?,
        @Query("size") size: String,
        @Query("sort") sort: String,
    ): ClubPagePostResponse

    //클럽 2뎁스 카테고리
    @GET("/club/{clubId}/category/{categoryCode}")
    suspend fun fetchClubSubCategory(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Query(ConstVariable.KEY_UID) integUid: String,
    ): ClubCategoryItem

    @GET("{path}")
    suspend fun fetchFreeBoardPost(
        @Path(value = "path", encoded = true) urlPath: String,
        @Query(ConstVariable.KEY_UID) integUid: String,
        @Query("nextId") nextId: String?,
        @Query("size") size: Int,
        @Query("sort") sort: String?
    ): ClubPagePostResponse

    @POST("club/main/check")
    suspend fun checkClubName(
        @Body clubName: HashMap<String, String>
    ): ClubNameCheckResponse

    @GET("/club/name")
    suspend fun checkClubName(
        @Query(value = "clubName",encoded = true) clubName: String
    ): ClubNameCheck

    //클럽 가입
    @POST("/club/{clubId}/join/member")
    suspend fun requestClubJoin(
        @Header("access_token") authToken: String?,
        @Path("clubId") clubId: String,
        @Body body: HashMap<String, Any>
    ) : ClubJoinResponse

    @GET("/club/{clubId}/member/nickname")
    suspend fun checkDuplicateNickName(
        @Path("clubId") clubId: String,
        @Query("nickname") nickName: String
    ): ClubNickNameDuplicateDataObj

    @GET("/club/{clubId}/member")
    suspend fun getClubMembersList(
        @Path("clubId") clubId: String,
        @QueryMap options :Map<String, String>
    ): ClubMembersListDto

    @GET("/club/{clubId}/member/search")
    suspend fun getClubMembersSearchList(
        @Path("clubId") clubId: String,
        @QueryMap options :Map<String, String>
    ): ClubMembersListDto

    @GET("/club/{clubId}/member/count")
    suspend fun getMemberCount(
        @Path("clubId") clubId: String,
        @Query("integUid") uid: String,
    ):ClubMemberCountDto

    @GET("/club/{clubId}/join/member/count")
    suspend fun getJoinWaitMemberCount(
        @Path("clubId") clubId: String,
        @Query("integUid") uid: String,
    ):ClubJoinWaitMemberCountDto

    @GET("/club/{clubId}/withdraw/member/count")
    suspend fun getWithDrawMemberCount(
        @Path("clubId") clubId: String,
        @Query("integUid") uid: String,
    ):ClubMemberCountDto

    @GET("/club/{clubId}/join/member")
    suspend fun getJoinWaitMemberList(
        @Path("clubId") clubId: String,
        @QueryMap options :Map<String, String>
    ): ClubJoinWaitListDto

    @GET("/club/{clubId}/withdraw/member")
    suspend fun getWithdrawMemberList(
        @Path("clubId") clubId: String,
        @QueryMap options :Map<String, String>
    ): ClubWithDrawMemberListDto

    @GET("/club/{clubId}/member/{memberId}")
    suspend fun getClubMemberInfo(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @Query("clubId") clubIdParam: String,
        @Query("integUid") integUid: String,
        @Query("memberId") memberIdParam: String
    ): ClubMemberInfoDto

    @GET("/club/{clubId}/member/{memberId}")
    suspend fun getClubMemberInfo(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: Int,
        @Query("integUid") integUid: String,
    ): ClubMemberInfoDto

    @GET("/club/{clubId}/storage/member/{memberId}/post")
    suspend fun getStoragePostList(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @QueryMap options :Map<String, String>
    ) :ClubStoragePostListDto

    @GET("/club/{clubId}/storage/member/{memberId}/reply")
    suspend fun getStorageReplyList(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @QueryMap options :Map<String, String>
    ): ClubStorageReplyListDto

    @GET("/club/{clubId}/storage/member/{memberId}/bookmark")
    suspend fun getStorageBookmarkList(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @QueryMap options :Map<String, String>
    ) : ClubStoragePostListDto

    @GET("/club/{clubId}/storage/count")
    suspend fun getClubStorageCount(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String
    ): ClubStorageCountDto

    @PATCH("/user/alim")
    suspend fun setClubAlarm(@Body requestData: HashMap<String, String>, @Query("clubId") clubId: String): ClubAlarmStateDto

    @GET("/club/{clubId}/alim")
    suspend fun getAlarmState(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String
    ):ClubAlarmStateDto

    @GET("/club/{clubId}/member/nickname")
    suspend fun checkClubMemberNickname(
        @Path("clubId") clubId: String,
        @Query("nickname") nickname: String
    ): ClubCheckNickNameDto

    @PATCH("/club/{clubId}/member/0")
    suspend fun modifyMyProfileOfClub(
        @Path("clubId") clubId: String,
        @Body requestData: HashMap<String, String>
    ): ClubMemberInfoDto

    @PATCH("/club/{clubId}/join/member/list/ok")
    suspend fun joinClubApprove(
        @Path("clubId") clubId: String,
        @Body requestData: HashMap<String, Any>
    )

    @HTTP(method = "DELETE", path = "/club/{clubId}/join/member/list/no", hasBody = true)
    suspend fun joinClubReject(
        @Path("clubId") clubId: String,
        @Body requestData: HashMap<String, Any>
    )

    @HTTP(method = "DELETE", path = "/club/{clubId}/member/0", hasBody = true)
    suspend fun leaveClub(
        @Path("clubId") clubId: String,
        @Body requestData: HashMap<String, String>
    )

    @GET("/club/interest")
    suspend fun getClubInterestCategory(): ClubInterestCategoryDataDto

    @GET("/club/{clubId}")
    suspend fun getClubAllInfo(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String
    ): ClubAllInfoDto

    @PATCH("/club/{clubId}")
    suspend fun setClubAllInfo(
        @Path("clubId") clubId: String,
        @Body requestData: HashMap<String, Any>
    ): ClubAllInfoDto

    @GET("/club/{clubId}/hashtag")//인자값 임시 소문자 -> integUid 로 변경되어야할것으로 보임
    suspend fun getHashTagList(
        @Path("clubId") clubId: String,
        @Query("integuid") integUid: String
    ): ClubHashTagListDto

    @POST("/club/{clubId}/hashtag")
    suspend fun setHashTagList(
        @Path("clubId") clubId: String,
        @Body requestData: HashMap<String, Any>
    ): ClubHashTagListDto

    @GET("/club/{clubId}/manage/info")
    suspend fun getClubManageInfo(@Path("clubId") clubId: String): ClubManageInfoDto

    @GET("/club/interest")
    suspend fun getClubCreateInterestList(): ClubInterestResponse

    @POST("club")
    suspend fun createClub(
        @Body body: HashMap<String, Any?>
    ): ClubCreateResponse

    @GET("/club/{clubId}/block/member/{memberId}")
    suspend fun getMemberBlockInfo(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @Query("integUid") integUid: String
    ): ClubMemberBlockInfoDto

    @PATCH("/club/{clubId}/block/member/{memberId}")
    suspend fun setMemberBlock(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @Body requestData: HashMap<String, String>
    ): ClubMemberBlockInfoDto

    @GET("/club/{clubId}/block/{categoryCode}/post/{postId}")
    suspend fun getPostBlockInfo(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId : String,
        @Query("integUid") integUid: String
    ): ClubPostBlockResult

    @PATCH("/club/{clubId}/block/{categoryCode}/post/{postId}")
    suspend fun setPostBlock(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId : String,
        @Body requestData: HashMap<String, Any>
    ): ClubPostBlockResult

    @GET("/club/{clubId}/block/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun getCommentBlockInfo(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId : String,
        @Path("replyId") replyId  : String,
        @Query("integUid") integUid: String
    ):ClubCommentBlockResult

    @PATCH("/club/{clubId}/block/{categoryCode}/post/{postId}/reply/{replyId}")
    suspend fun setCommentBlock(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId : String,
        @Path("replyId") replyId  : String,
        @Body requestData: HashMap<String, Any>
    ): ClubCommentBlockResult

    @GET("/club/{clubId}/block/{categoryCode}/post/{postId}/reply/{parentReplyId}/replys/{replyId}")
    suspend fun getSubCommentBlockInfo(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId : String,
        @Path("parentReplyId") parentReplyId   : String,
        @Path("replyId") replyId  : String,
        @Query("integUid") integUid: String
    ): ClubCommentBlockResult

    @PATCH("/club/{clubId}/block/{categoryCode}/post/{postId}/reply/{parentReplyId}/replys/{replyId}")
    suspend fun setSubCommentBlock(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Path("postId") postId : String,
        @Path("parentReplyId") parentReplyId   : String,
        @Path("replyId") replyId  : String,
        @Body requestData: HashMap<String, Any>
    ) : ClubCommentBlockResult

    @GET("/club/{clubId}/setting/category")
    suspend fun getClubSettingCategoryList(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String
    ): ClubCategoryResponse

    @PATCH("/club/{clubId}/setting/category/{categoryCode}/list/order")
    suspend fun setCategoryListOrder(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Body requestData: HashMap<String, Any>
    ): Unit

    @PATCH("/club/{clubId}/setting/category/{categoryCode}")
    suspend fun modifySettingCategory(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Body requestData: HashMap<String, Any>
    ): Unit

    @POST("/club/{clubId}/setting/category/{categoryCode}")
    suspend fun createSettingCategory(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Body requestData: HashMap<String, Any>
    ): ClubCategoryItem

    @HTTP(
        method = "DELETE",
        path = "/club/{clubId}/setting/category/{categoryCode}",
        hasBody = true
    )
    suspend fun deleteSettingCategory(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Body requestData: HashMap<String, Any>
    ): Unit

    @PATCH("/club/{clubId}/setting/category/{categoryCode}/list")
    suspend fun saveFreeboardOpenWord(
        @Path("clubId") clubId: String,
        @Path("categoryCode") categoryCode: String,
        @Body requestData: HashMap<String, Any>
    ): ClubCategoryItem

    @GET("/club/{clubId}/closes")
    suspend fun getClubCloseStateInfo(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String
    ): ClubCloseStateDto

    @POST("/club/{clubId}/closes")
    suspend fun requestClubClose(
        @Path("clubId") clubId: String,
        @Body requestData: HashMap<String, Any>
    )

    @PATCH("/club/{clubId}/closes/cancel")
    suspend fun cancelClubClose(
        @Path("clubId") clubId: String,
        @Body requestData: HashMap<String, Any>
    )

    @PATCH("/club/{clubId}/delegate/member/{memberId}/request")
    suspend fun requestClubDelegate(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @Body requestData: HashMap<String, Any>
    ): ClubCloseDelegatingDto

    @PATCH("/club/{clubId}/delegate/member/{memberId}/cancel")
    suspend fun cancelClubDelegating(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @Body requestData: HashMap<String, Any>
    ): ClubCloseDelegatingDto

    @GET("/club/{clubId}/delegate/member")
    suspend fun getClubDelegatingInfo(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String,
    ):ClubDelegatingInfoDto

    @GET("/club/{clubId}/delegate/member/0")
    suspend fun getClubDelegateOfMe(
        @Path("clubId") clubId: String,
        @Query("integUid") integUid: String,
    ):ClubDelegatingInfoDto

    @PATCH("/club/{clubId}/delegate/member/0/ok")
    suspend fun acceptClubMasterDelegate(
        @Path("clubId") clubId: String,
        @Body hashBody: HashMap<String,Any>,
    )

    @PATCH("/club/{clubId}/delegate/member/0/no")
    suspend fun rejectClubMasterDelegate(
        @Path("clubId") clubId: String,
        @Body hashBody: HashMap<String,Any>,
    )

    @POST("/club/{clubId}/withdraw/member/{memberId}")
    suspend fun requestMemberBan(
        @Path("clubId") clubId: String,
        @Path("memberId") memberId: String,
        @Body requestData: HashMap<String, Any>
    )

    @PATCH("/club/{clubId}/withdraw/member/{withdrawId}")
    suspend fun updateWithdrawMember(
        @Path("clubId") clubId: String,
        @Path("withdrawId") withdrawId: String,
        @Body requestData: HashMap<String, Any>
    ):ClubMemberWithdrawDto

    // Storage
    @GET("/club/storage/post")
    suspend fun fetchClubMyPosts(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: String,
        @Query("size") size: String,
    ): ClubStoragePostListDto

    @GET("/club/storage/reply")
    suspend fun fetchClubMyComments(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: String,
        @Query("size") size: String,
    ): ClubStorageReplyListDto

    @GET("/club/storage/bookmark")
    suspend fun fetchClubMyBookmarks(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: String,
        @Query("size") size: String,
    ): ClubStoragePostListDto

    //Challenge
    @GET("/club/challenge/post")
    suspend fun getChallengePost(
        @Query(ConstVariable.KEY_UID)integUid :String,
        @Query("size")size : Int,
        @Query("nextId")nextId: Int?
    ) : ChallengePostResponse

    //Archive
}