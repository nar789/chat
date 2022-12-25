package com.rndeep.fns_fantoo.repositories

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ChatUserService
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

class ChatUserRepository @Inject constructor(
    private val chatUserApi: ChatUserService
) : BaseNetRepo() {

    suspend fun isUserBlocked(myUserId: String, userId: String): Boolean {
        safeApiCall(Dispatchers.IO) { chatUserApi.getUserBlockState(myUserId, userId) }.run {
            Timber.d("isUserBlocked: $this")
            return when (this) {
                is ResultWrapper.Success -> this.data
                else -> -1
            } == BLOCKED
        }
    }

    suspend fun isConversationBlocked(myUserId: String, conversationId: Long): Boolean {
        safeApiCall(Dispatchers.IO) {
            chatUserApi.getConversationBlockState(myUserId, conversationId)
        }.run {
            Timber.d("isConversationBlocked: $this")
            return when (this) {
                is ResultWrapper.Success -> this.data
                else -> -1
            } == BLOCKED
        }
    }

    suspend fun isUserFollowed(myUserId: String, userId: String): Boolean {
        safeApiCall(Dispatchers.IO) { chatUserApi.getUserFollowState(myUserId, userId) }.run {
            Timber.d("isUserFollowed: $this")
            return when (this) {
                is ResultWrapper.Success -> this.data
                else -> -1
            } == FOLLOWED
        }
    }

    suspend fun setUserBlocked(myUserId: String, userId: String, block: Boolean) {
        safeApiCall(Dispatchers.IO) {
            chatUserApi.setUserBlockState(myUserId, userId, if (block) 1 else 0)
        }.run {
            Timber.d("setUserBlocked: $this")
        }
    }

    suspend fun setConversationBlocked(myUserId: String, conversationId: Long, block: Boolean) {
        safeApiCall(Dispatchers.IO) {
            chatUserApi.setConversationBlockState(myUserId, conversationId, if (block) 1 else 0)
        }.run {
            Timber.d("setConversationBlocked: $this")
        }
    }

    suspend fun setUserFollowed(myUserId: String, userId: String, follow: Boolean) {
        safeApiCall(Dispatchers.IO) {
            chatUserApi.setUserFollowState(myUserId, userId, if (follow) 1 else 0)
        }.run {
            Timber.d("setUserFollowed: $this")
        }
    }

    companion object {
        const val BLOCKED = 1
        const val FOLLOWED = 1
    }
}