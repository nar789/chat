package com.rndeep.fns_fantoo.repositories

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ChatService
import com.rndeep.fns_fantoo.data.remote.dto.ChatUserInfoResponse
import com.rndeep.fns_fantoo.data.remote.dto.TargetIntegUid
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

// TODO: implement below method
class ChatUserRepository @Inject constructor(
    private val chatApi: ChatService
) : BaseNetRepo() {

    /*        safeApiCall(Dispatchers.IO) {
            chatUserApi.getConversationBlockState(myUserId, conversationId)
        }.run {
            Timber.d("isConversationBlocked: $this")
            return when (this) {
                is ResultWrapper.Success -> this.data
                else -> -1
            } == BLOCKED
        }*/

    suspend fun isUserBlocked(myUserId: String, userId: String): Boolean {
        return false
    }

    suspend fun isConversationBlocked(myUserId: String, conversationId: Int): Boolean {
        return false
    }

    suspend fun isUserFollowed(myUserId: String, userId: String): Boolean {
        return true
    }

    suspend fun setUserBlocked(myUserId: String, userId: String, block: Boolean) {

    }

    suspend fun setConversationBlocked(myUserId: String, conversationId: Int, block: Boolean) {

    }

    suspend fun setUserFollowed(myUserId: String, userId: String, follow: Boolean) {

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
        }
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
        }
    }

    suspend fun fetchChatUserInfo(
        accessToken: String,
        targetIntegUid: TargetIntegUid
    ): ResultWrapper<ChatUserInfoResponse> = safeApiCall(Dispatchers.IO) {
        chatApi.fetchChatUserInfo(accessToken, targetIntegUid)
    }.also { logCallResult("fetchChatUserInfo", it) }

    private fun <T> logCallResult(callName: String, resultWrapper: ResultWrapper<T>) {
        if (resultWrapper is ResultWrapper.Success) {
            Timber.d("$callName: ${resultWrapper.data}")
        }
    }
}