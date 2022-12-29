package com.rndeep.fns_fantoo.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ChatService
import com.rndeep.fns_fantoo.data.remote.dto.ChatUserInfoResponse
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse
import com.rndeep.fns_fantoo.data.remote.dto.TargetIntegUid
import com.rndeep.fns_fantoo.ui.chatting.addchat.AddChatFollowDataSource
import com.rndeep.fns_fantoo.ui.chatting.addchat.AddChatSearchDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

// TODO: implement below method
class ChatUserRepository @Inject constructor(
    private val chatApi: ChatService
) : BaseNetRepo() {

    suspend fun isConversationBlocked(myUserId: String, conversationId: Int): Boolean {
        return false
    }

    suspend fun setConversationBlocked(myUserId: String, conversationId: Int, block: Boolean) {

    }

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
        query: String
    ): Flow<PagingData<GetUserListResponse.ChatUserDto>> = Pager(PagingConfig(pageSize = 10)) {
        AddChatSearchDataSource(
            chatService = chatApi, accessToken = accessToken, query = query
        )
    }.flow
}