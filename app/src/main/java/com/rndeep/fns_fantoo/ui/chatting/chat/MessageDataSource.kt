package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

class MessageDataSource(
    private val loadMessageFlow: Flow<List<Message>>,
    private val cachedMessages: MutableList<Message>,
    private var useCache: Boolean,
    private var requestLoadMessage: (Int, Int) -> Unit,
    private var requestReadInfo: () -> Unit
) : PagingSource<Int, Message>() {

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let { anchorPos ->
            val anchorPage = state.closestPageToPosition(anchorPos) ?: return@let null
            val pageDataSize = anchorPage.data.size
            anchorPage.prevKey?.plus(pageDataSize) ?: anchorPage.nextKey?.minus(pageDataSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        // return cachedMessages if exist
        if (useCache && cachedMessages.isNotEmpty()) {
            useCache = false
            requestReadInfo()
            return LoadResult.Page(
                data = cachedMessages.toList(),
                prevKey = null,
                nextKey = cachedMessages.size
            )
        }

        val offset = params.key ?: 0
        return try {
            requestLoadMessage(offset, params.loadSize)

            val messages = getLoadMessages()
            cachedMessages.addAll(messages)
            LoadResult.Page(
                data = messages,
                prevKey = null,
                nextKey = if (messages.isEmpty()) null else offset + messages.size
            )
        } catch (e: Exception) {
            Timber.e("request LoadMessage: ${e.message}", e)
            LoadResult.Error(e)
        }
    }

    private suspend fun getLoadMessages(): List<Message> {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            loadMessageFlow.first()
        }
    }
}