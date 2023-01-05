package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.*
import retrofit2.http.*

interface ChatService {

    @POST("/$PATH_CHAT/block")
    suspend fun setUserBlockState(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Body targetIntegUid: TargetIntegUid
    )

    @HTTP(method="DELETE", hasBody=true, path="/$PATH_CHAT/block")
    suspend fun deleteUserBlockState(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Body targetIntegUid: TargetIntegUid
    )

    @POST("/$PATH_CHAT/follow")
    suspend fun setUserFollow(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Body targetIntegUid: TargetIntegUid
    )

    @HTTP(method="DELETE", hasBody=true, path="/$PATH_CHAT/follow")
    suspend fun deleteUserFollow(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Body targetIntegUid: TargetIntegUid
    )

    @GET("/$PATH_CHAT/my/follow")
    suspend fun getMyFollowList(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: Int,
        @Query("size") size: Int,
    ): GetUserListResponse

    @GET("/$PATH_CHAT/search")
    suspend fun getSearchList(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Query("integUid") integUid: String,
        @Query("nextId") nextId: Int,
        @Query("search") search: String,
        @Query("size") size: Int,
    ): GetUserListResponse

    @POST("/$PATH_CHAT/user/info")
    suspend fun fetchChatUserInfo(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Body targetIntegUid: TargetIntegUid
    ): ChatUserInfoResponse

    @GET("/$PATH_CHAT/conversation/block")
    suspend fun getConversationBlock(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Query("conversationId ") conversationId: String,
        @Query("integUid") integUid: String
    ): ConversationBlockResponse

    @POST("/$PATH_CHAT/conversation/block")
    suspend fun blockConversation(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Body chatConversationDto: ChatConversationDto
    ): ConversationBlockResponse

    @HTTP(method="DELETE", hasBody=true, path="/$PATH_CHAT/conversation/block")
    suspend fun deleteConversationBlock(
        @Header(HEADER_ACCESS_TOKEN) accessToken: String,
        @Body chatConversationDto: ChatConversationDto
    ): ConversationBlockResponse

    private companion object {
        const val PATH_CHAT = "chat"
        const val HEADER_ACCESS_TOKEN = "access_token"
    }
}