package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rndeep.fns_fantoo.data.remote.api.ChatService
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse
import timber.log.Timber

class AddChatSearchDataSource(
    private val chatService: ChatService,
    private val uId: String,
    private val accessToken: String,
    private val query: String
) : PagingSource<Int, GetUserListResponse.ChatUserDto>() {
    override fun getRefreshKey(state: PagingState<Int, GetUserListResponse.ChatUserDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetUserListResponse.ChatUserDto> {
        val currentKey = params.key ?: 0
        val loadSize = params.loadSize

        try {
            val response = chatService.getSearchList(accessToken, uId, currentKey, query, loadSize)
            val data = response.chatUserDtoList
            val nextKey = response.nextId
            val listSize = response.listSize

            Timber.d("[Add Chat] search list: $data, $nextKey, $listSize")

            return LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = if (listSize == 0 || data.isEmpty() || nextKey < 0) null else nextKey
            )
        } catch (e: Exception) {
            Timber.e("add chat search exception: ${e.message}", e)
            return LoadResult.Error(e)
        }
    }
}