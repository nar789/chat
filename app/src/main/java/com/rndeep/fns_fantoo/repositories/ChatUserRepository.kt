package com.rndeep.fns_fantoo.repositories

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.api.ChatService
import javax.inject.Inject

// TODO: implement below method
class ChatUserRepository @Inject constructor(
    private val chatUserApi: ChatService
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
}