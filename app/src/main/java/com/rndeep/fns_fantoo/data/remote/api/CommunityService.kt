package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.CommunityMyPostResponse
import com.rndeep.fns_fantoo.data.remote.dto.CommunityMyReplyResponse
import com.rndeep.fns_fantoo.data.remote.dto.ComposeCommunityPost
import com.rndeep.fns_fantoo.data.remote.model.community.*
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostDataObj
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_ACCESS_TOKEN
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import retrofit2.http.*

interface CommunityService {
    //Community PostItem
    @GET("/community/post/popular")
    suspend fun getRealHotCommunityItem(
        @Header(KEY_ACCESS_TOKEN) accessToken: String,
        @Query(KEY_UID) uId: String
    ): BoardMainPostData

    //Community PostItem (비회원)
    @GET("/community/guest/post/popular")
    suspend fun getRealHotCommunityItem(): BoardMainPostData

    //Community 상단 회원 게시판[팬투 추천순]
    @GET("/community/category")
    suspend fun getBoardListOfMemberFantoo(
        @Header(KEY_ACCESS_TOKEN) accessToken: String,
        @Query(KEY_UID) uid: String
    ): CommunityBoardCategoryBody

    //Community 상단 비회원 게시판[팬투 추천순]
    @GET("/community/guest/category")
    suspend fun getBoardListOfGuest(): CommunityBoardCategoryBody

    //Community 상단 회원 게시판[인기 순]
    @GET("/community/category/popular")
    suspend fun getBoardListOfMemberPopular(
        @Header(KEY_ACCESS_TOKEN) accessToken: String,
        @Query(KEY_UID) uid: String
    ): CommunityBoardCategoryBody

    //즐겨찾기 추가
    @POST("/community/category/favorite")
    suspend fun registerBookmark(@Header(KEY_ACCESS_TOKEN)authToken :String, @Body body :HashMap<String,Any>)

    //즐겨찾기 삭제
    @HTTP(method="DELETE", hasBody=true, path="/community/category/favorite")
    suspend fun deleteBookmark(@Header(KEY_ACCESS_TOKEN)authToken :String, @Body body : HashMap<String,Any>)


    //Community 공지 게시글
    @GET("/community/notice/top")
    suspend fun getCommunityTopNotices(): CommunityNoticeDataObj

    //Community 게시글 검색 (비회원)
    @GET("/community/guest/post/search")
    suspend fun callSearchCommunityPost(
        @Query("search") searchWord: String,
        @Query("nextId") nextId: Int,
        @Query("size") size: Int
    ): BoardPostDataObj

    //Community 게시글 검색 (회원)
    @GET("/community/post/search")
    suspend fun callSearchCommunityPost(
        @Header(KEY_ACCESS_TOKEN) accessToken: String,
        @Query(KEY_UID) integUid: String,
        @Query("search") searchWord: String,
        @Query("nextId") nextId: Int,
        @Query("size") size: Int
    ): BoardPostDataObj


    //게시판 카테고리(회원)
    @GET("/community/category/{code}")
    suspend fun getBoardCategory(
        @Header(KEY_ACCESS_TOKEN) accessToken: String,
        @Path("code") code: String,
        @Query(ConstVariable.KEY_UID)uis : String,
    ) : BoardSubCategory

    //게시판 카테고리(비회원)
    @GET("/community/guest/category/{code}")
    suspend fun getGuestBoardCategory(
        @Path("code") code: String,
    ) : BoardSubCategory

    //게시판 공지 상단 노출
    @GET("/community/{code}/notice/top")
    suspend fun getBoardNoticeTop(
        @Path("code") code :String
    ) : CommunityNoticeDataObj

    //게시판 전체 공지 리스트(paging)
    @GET("/community/{code}/notice")
    suspend fun getBoardNoticeList(
        @Path("code") code : String,
        @Query("nextId")nextId :Int,
        @Query("size")size : Int,
    ) : CommunityNoticeDataObj

    //커뮤니티 메인 화면 더보기 공지(paging)
    @GET("/community/notice")
    suspend fun getCommonBoardNotice(
        @Query("nextId")nextId :Int,
        @Query("size")size : Int
    ) : CommunityNoticeDataObj

    //비회원 게시판 게시글 리스트
    @GET("/community/guest/{code}/post")
    suspend fun getBoardPostGuest(
        @Path("code") code : String,
        @Query("globalYn") globalYn :Boolean,
        @Query("nextId") nextId :Int,
        @Query("size") size : Int,
        @Query("subCode") subCode : String
    ) : BoardPostDataObj

    @GET("/community/guest/{code}/post")
    suspend fun getBoardPostGuest(
        @Path("code") code : String,
        @Query("globalYn") globalYn :Boolean,
        @Query("nextId") nextId :Int,
        @Query("size") size : Int,
    ) : BoardPostDataObj

    //회원 게시판 게시글 리스트
    @GET("/community/{code}/post")
    suspend fun getBoardPost(
        @Header(KEY_ACCESS_TOKEN) accessToken: String,
        @Path("code") code : String,
        @Query("globalYn") globalYn :Boolean,
        @Query(KEY_UID) integUid :String,
        @Query("nextId") nextId :Int,
        @Query("size") size : Int,
        @Query("subCode") subCode : String
    ) : BoardPostDataObj

    @GET("/community/{code}/post")
    suspend fun getBoardPost(
        @Header(KEY_ACCESS_TOKEN) accessToken: String,
        @Path("code") code : String,
        @Query("globalYn") globalYn :Boolean,
        @Query(KEY_UID) integUid :String,
        @Query("nextId") nextId :Int,
        @Query("size") size : Int,
    ) : BoardPostDataObj

    @POST("community/{code}/post")
    suspend fun composeCommunityPost(
        @Header("access_token") accessToken: String,
        @Body composeCommunityPost: ComposeCommunityPost,
        @Path("code") code: String,
    )

    @GET("community/my/post")
    suspend fun fetchCommunityMyPosts(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: Int,
        @Query("size") size: Int,
    ): CommunityMyPostResponse

    @GET("community/my/reply")
    suspend fun fetchCommunityMyComments(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: Int,
        @Query("size") size: Int,
    ): CommunityMyReplyResponse

    @GET("community/my/bookmark")
    suspend fun fetchCommunityMyBookmarks(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: Int,
        @Query("size") size: Int,
    ): CommunityMyPostResponse
}