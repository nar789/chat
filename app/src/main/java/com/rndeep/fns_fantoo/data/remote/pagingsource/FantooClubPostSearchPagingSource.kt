package com.rndeep.fns_fantoo.data.remote.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rndeep.fns_fantoo.data.remote.api.FantooClubService
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost
import com.rndeep.fns_fantoo.di.NetworkModule
import javax.inject.Inject

class FantooClubPostSearchPagingSource @Inject constructor(
    @NetworkModule.ApiServer private val fantooClubService: FantooClubService,
    private val clubId: String,
    private val keyword: String
) : PagingSource<Int, FantooClubPost>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FantooClubPost> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val pageId = if (page == STARTING_PAGE_INDEX) null else page.toString()
            val response = fantooClubService.searchFantooClubPosts(
                clubId,
                keyword,
                pageId,
                NETWORK_PAGE_SIZE.toString()
            )
            val posts = response.postList
            val nextKey = response.nextId
            LoadResult.Page(
                data = posts,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FantooClubPost>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 0
        private const val NETWORK_PAGE_SIZE = 20
    }
}