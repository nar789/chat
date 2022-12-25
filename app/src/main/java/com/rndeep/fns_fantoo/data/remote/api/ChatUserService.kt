package com.rndeep.fns_fantoo.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path

interface ChatUserService {

    @GET("/block/{myUserId}/{userId}")
    suspend fun getUserBlockState(
        @Path("myUserId") myUserId: String,
        @Path("userId") userId: String
    ): Int

    @GET("/follow/{myUserId}/{userId}")
    suspend fun getUserFollowState(
        @Path("myUserId") myUserId: String,
        @Path("userId") userId: String
    ): Int

    @GET("/blockconversation/{myUserId}/{conversationId}")
    suspend fun getConversationBlockState(
        @Path("myUserId") myUserId: String,
        @Path("conversationId") conversationId: Int
    ): Int

    @GET("/block/{myUserId}/{userId}/{status}")
    suspend fun setUserBlockState(
        @Path("myUserId") myUserId: String,
        @Path("userId") userId: String,
        @Path("status") status: Int
    ): String

    @GET("/follow/{myUserId}/{userId}/{status}")
    suspend fun setUserFollowState(
        @Path("myUserId") myUserId: String,
        @Path("userId") userId: String,
        @Path("status") status: Int
    ): String

    @GET("/blockconversation/{myUserId}/{conversationId}/{status}")
    suspend fun setConversationBlockState(
        @Path("myUserId") myUserId: String,
        @Path("conversationId") conversationId: Int,
        @Path("status") status: Int
    ): String
}