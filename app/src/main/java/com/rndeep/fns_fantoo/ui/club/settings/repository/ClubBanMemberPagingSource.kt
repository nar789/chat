package com.rndeep.fns_fantoo.ui.club.settings.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.dto.ClubWithDrawMemberInfoWithMeta
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_LIST_REQUEST_SIZE
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_LIST_NEXTID
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import timber.log.Timber
import javax.inject.Inject

class ClubBanMemberPagingSource @Inject constructor(
    @NetworkModule.ApiServer private val clubService: ClubService,
    private val clubId: String,
    private val uid: String
) : PagingSource<Int, ClubWithDrawMemberInfoWithMeta>() {
    override fun getRefreshKey(state: PagingState<Int, ClubWithDrawMemberInfoWithMeta>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClubWithDrawMemberInfoWithMeta> {
        try {
            var nextId: Int? = params.key ?: 0
            val map = hashMapOf(KEY_UID to uid,
            ConstVariable.ClubDef.KEY_CLUB_LIST_SIZE to CLUB_LIST_REQUEST_SIZE.toString())
            if(nextId != 0){
                map[KEY_CLUB_LIST_NEXTID] to nextId.toString()
            }
            val results =
                clubService.getWithdrawMemberList(clubId, map)
            nextId = results.nextId//if (results.nextId == nextId) null else results.nextId
            val data = results.withdrawList.map {
                ClubWithDrawMemberInfoWithMeta(
                    clubWithDrawMemberInfo = it,
                    totalMemberCount = results.totalMemberCount,
                    listSize = results.listSize
                )
            }
            return LoadResult.Page(data = data, prevKey = null, nextKey = nextId)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}