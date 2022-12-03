package com.rndeep.fns_fantoo.ui.club.settings.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoWithMeta
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubJoinWaitMemberWithMeta
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import timber.log.Timber
import javax.inject.Inject

class ClubJoinWaitMemberPagingSource @Inject constructor(
    @NetworkModule.ApiServer private val clubService: ClubService,
    private val clubId:String,
    private val uid:String) : PagingSource<Int, ClubJoinWaitMemberWithMeta>(){

    override fun getRefreshKey(state: PagingState<Int, ClubJoinWaitMemberWithMeta>): Int? {
        return state.anchorPosition?.let {
                anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClubJoinWaitMemberWithMeta> {
        try {
            var nextId: Int? = params.key ?: 0
            val map = hashMapOf(
                ConstVariable.KEY_UID to uid,
                ConstVariable.ClubDef.KEY_CLUB_LIST_SIZE to ConstVariable.ClubDef.CLUB_LIST_REQUEST_SIZE.toString(),
            )
            if (nextId != 0) {
                map[ConstVariable.ClubDef.KEY_CLUB_LIST_NEXTID] = nextId.toString()
            }
            val results =
                clubService.getJoinWaitMemberList(clubId, map)
            nextId = if (results.nextId == nextId) null else results.nextId
            val data = results.joinList.map {
                ClubJoinWaitMemberWithMeta(
                    clubJoinWaitMember = it,
                    listSize = results.listSize
                )
            }
            return LoadResult.Page(data = data, prevKey = null, nextKey = nextId)
        }catch (e : Exception){
            return LoadResult.Error(e)
        }
    }
}