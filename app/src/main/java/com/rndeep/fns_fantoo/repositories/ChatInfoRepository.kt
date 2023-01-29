package com.rndeep.fns_fantoo.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ChatService
import com.rndeep.fns_fantoo.data.remote.dto.*
import com.rndeep.fns_fantoo.ui.chatting.addchat.AddChatFollowDataSource
import com.rndeep.fns_fantoo.ui.chatting.addchat.AddChatSearchDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

// TODO: implement below method
class ChatInfoRepository @Inject constructor(
    private val chatApi: ChatService
) : BaseNetRepo() {

    suspend fun isConversationBlocked(
        accessToken: String,
        myUid: String,
        conversationId: Int
    ): ResultWrapper<ConversationBlockResponse> = safeApiCall(Dispatchers.IO) {
        chatApi.getConversationBlock(accessToken, conversationId.toString(), myUid)
    }.also { logCallResult("isConversationBlocked", it) }

    suspend fun setConversationBlocked(
        accessToken: String,
        myUid: String,
        conversationId: Int,
        block: Boolean
    ): ResultWrapper<ConversationBlockResponse> = safeApiCall(Dispatchers.IO) {
        val dto = ChatConversationDto(conversationId.toString(), myUid)
        if (block) {
            chatApi.blockConversation(accessToken, dto)
        } else {
            chatApi.deleteConversationBlock(accessToken, dto)
        }
    }.also { logCallResult("setConversationBlocked", it) }

    suspend fun setUserBlock(
        accessToken: String,
        myUid: String,
        targetUid: String,
        block: Boolean
    ) {
        safeApiCall(Dispatchers.IO) {
            val targetUidDto = TargetIntegUid(myUid, targetUid)
            if (block) {
                chatApi.setUserBlockState(accessToken, targetUidDto)
            } else {
                chatApi.deleteUserBlockState(accessToken, targetUidDto)
            }
        }.also { logCallResult("setUserBlock", it) }
    }

    suspend fun setUserFollow(
        accessToken: String,
        myUid: String,
        targetUid: String,
        follow: Boolean
    ) {
        safeApiCall(Dispatchers.IO) {
            val targetUidDto = TargetIntegUid(myUid, targetUid)
            if (follow) {
                chatApi.setUserFollow(accessToken, targetUidDto)
            } else {
                chatApi.deleteUserFollow(accessToken, targetUidDto)
            }
        }.also { logCallResult("setUserFollow", it) }
    }

    suspend fun fetchChatUserInfo(
        accessToken: String,
        myUid: String,
        targetUid: String
    ): ResultWrapper<ChatUserInfoResponse> = safeApiCall(Dispatchers.IO) {
        val targetUidDto = TargetIntegUid(myUid, targetUid)
        chatApi.fetchChatUserInfo(accessToken, targetUidDto)
    }.also { logCallResult("fetchChatUserInfo", it) }

    private fun <T> logCallResult(callName: String, result: ResultWrapper<T>) {
        when (result) {
            is ResultWrapper.Success -> Timber.d("$callName: ${result.data}")
            is ResultWrapper.GenericError -> Timber.e("$callName response error code : ${result.code} , server msg : ${result.message} , message : ${result.errorData?.message}")
            is ResultWrapper.NetworkError -> Timber.e("$callName NetworkError")
        }
    }

    fun getMyFollowList(
        integUid: String,
        accessToken: String
    ): Flow<PagingData<GetUserListResponse.ChatUserDto>> = Pager(PagingConfig(pageSize = 10)) {
        AddChatFollowDataSource(
            chatApi, integUid, accessToken
        )
    }.flow


    fun getSearchList(
        accessToken: String,
        integUid: String,
        query: String
    ): Flow<PagingData<GetUserListResponse.ChatUserDto>> = Pager(PagingConfig(pageSize = 10)) {
        AddChatSearchDataSource(
            chatService = chatApi, uId = integUid, accessToken = accessToken, query = query
        )
    }.flow
}