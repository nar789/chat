package com.rndeep.fns_fantoo.ui.chatting.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketEvent
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber

class MessageDataSource(
    private val socketManager: ChatSocketManager,
    private val conversationId: Int,
    private val loadMessageFlow: Flow<List<Message>>,
    private val snapshots: MutableList<Message>,
    private var useSnapshot: Boolean
) : PagingSource<Int, Message>() {

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let { anchorPos ->
            val anchorPage = state.closestPageToPosition(anchorPos)
            val loadSize = anchorPage?.data?.size ?: 10
            anchorPage?.prevKey?.plus(loadSize) ?: anchorPage?.nextKey?.minus(loadSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        if (useSnapshot) {
            Log.d("inwha", "useSnapshot: ${snapshots.size}")
            useSnapshot = false
            return LoadResult.Page(
                data = snapshots,
                prevKey = snapshots.size,
                nextKey = null
            )
        }
        val offset = params.key ?: 0
        val loadSize = params.loadSize
        Log.d("sujini", "load: $offset, $loadSize")

        try {
            requestLoadMessage(offset, loadSize)

            val messages = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                loadMessageFlow
                    .onEach { Log.d("sujini", "flow onEach ${it.size}") }
                    .first()
            }
            if (offset == 0) {
                snapshots.clear()
                Log.d("inwha", "offset is 0")
            }
            Log.d("inwha", "addSnapshot: ${messages.size}, $offset")
            snapshots.addAll(0, messages)
            Log.d("sujini", "---------------------------offset: $offset")
            messages.forEach {
                Log.d("sujini", "$it")
            }
            return LoadResult.Page(
                data = messages,
                prevKey = if (messages.isEmpty()) null else offset + messages.size,
                nextKey = null
            )
        } catch (e: Exception) {
            Log.e("sujini", "$e")
            Timber.e("request LoadMessage: ${e.message}", e)
            return LoadResult.Error(e)
        }
    }

    private fun requestLoadMessage(offset: Int, loadSize: Int) {
        socketManager.emit(
            ChatSocketEvent.LOAD_MESSAGE,
            mapOf(
                PARAM_CONVERSATION_ID to conversationId.toString(),
                PARAM_OFFSET to offset.toString(),
                PARAM_SIZE to loadSize.toString()
            ).also { Log.d("sujini", "requestLoadMessage: $it") }
        )
    }

    fun updateNewMessage(message: Message) {
        snapshots.add(message)
        useSnapshot = true
        invalidate()
    }

    private companion object {
        const val PARAM_CONVERSATION_ID = "conversationId"
        const val PARAM_OFFSET = "offset"
        const val PARAM_SIZE = "size"
    }
}