package com.rndeep.fns_fantoo.ui.club.settings.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoDto
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoWithMeta
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_LIST_REQUEST_SIZE
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_LIST_KEYWORD
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_LIST_NEXTID
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_LIST_SIZE
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import timber.log.Timber
import javax.inject.Inject


class ClubMembersPagingSource @Inject constructor(
    @NetworkModule.ApiServer private val clubService: ClubService,
    private val clubId: String,
    private val uid: String,
    private val keyword: String
) : PagingSource<Int, ClubMemberInfoWithMeta>() {
    override fun getRefreshKey(state: PagingState<Int, ClubMemberInfoWithMeta>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClubMemberInfoWithMeta> {
        try {
            if (keyword.isNullOrEmpty()) {
                var nextId: Int? = params.key ?: 0
                val map = hashMapOf(
                    KEY_UID to uid,
                    KEY_CLUB_LIST_SIZE to CLUB_LIST_REQUEST_SIZE.toString(),
                )
                if (nextId != 0) {
                    map[KEY_CLUB_LIST_NEXTID] = nextId.toString()
                }
                val results =
                    clubService.getClubMembersList(
                        clubId,
                        map
                    )
                nextId = if (results.nextId == nextId) null else results.nextId
                val data = results.memberList.map {
                    ClubMemberInfoWithMeta(
                        it,
                        results.memberCount,
                        results.totalSearchCnt,
                        results.listSize
                    )
                }
                return LoadResult.Page(data = data, prevKey = null, nextKey = nextId)
            } else {
                var nextId: Int? = params.key ?: 0
                val map = hashMapOf(
                    KEY_UID to uid,
                    KEY_CLUB_LIST_KEYWORD to keyword,
                    KEY_CLUB_LIST_SIZE to CLUB_LIST_REQUEST_SIZE.toString(),
                )
                if (nextId != 0) {
                    map[KEY_CLUB_LIST_NEXTID] = nextId.toString()
                }
                val results =
                    clubService.getClubMembersSearchList(
                        clubId,
                        map
                    )
                nextId = if (results.nextId == nextId) null else results.nextId
                val data = results.memberList.map {
                    ClubMemberInfoWithMeta(
                        it,
                        results.memberCount,
                        results.totalSearchCnt,
                        results.listSize
                    )
                }
                return LoadResult.Page(data = data, prevKey = null, nextKey = nextId)
            }
            /*}catch (thr :Throwable){
                Timber.d("thr $thr")
                return LoadResult.Error(thr)*/
        } catch (e: Exception) {
            Timber.e("err : ${e.printStackTrace()}, ${e.message}")
            return LoadResult.Error(e)
        }
    }
}