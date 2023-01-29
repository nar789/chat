package com.rndeep.fns_fantoo.ui.community

import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.CommunityService
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.data.remote.dto.CommunityMyPostResponse
import com.rndeep.fns_fantoo.data.remote.dto.ComposeCommunityPost
import com.rndeep.fns_fantoo.data.remote.model.community.BoardSubCategory
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityBoardCategoryBody
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CommunityRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    @NetworkModule.ApiServer private val communityService: CommunityService,
    @NetworkModule.ApiServer private val postService: PostService
) : PostRepository(boardPagePostDao,postService) {

    //커뮤니티 real top 아이템
    suspend fun getCommunityRealTimeItem(accessToken: String?,uId: String?) : Pair<List<BoardPagePosts>?,ErrorBody?> {
        val safeRes = if(accessToken ==null || uId == null) {
            safeApiCall(Dispatchers.IO) { communityService.getRealHotCommunityItem() }
        }else{
            safeApiCall(Dispatchers.IO) { communityService.getRealHotCommunityItem(accessToken,uId) }
        }
        boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_COMMUNITYPAGE)
        val tempCommunityDatas = ArrayList<BoardPagePosts>()
        tempCommunityDatas.add( BoardPagePosts(
            type = ConstVariable.TYPE_BOARD,
            boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE)
        )
        tempCommunityDatas.add(
            BoardPagePosts(
                type = ConstVariable.TYPE_COMMUNITY_NOTICE,
                boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE)
        )
        tempCommunityDatas.add(
            BoardPagePosts(
                type = ConstVariable.TYPE_REALTIME,
                boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE)
        )
        when (safeRes) {
            is ResultWrapper.Success -> {
                    for (a in safeRes.data.hourPost){
                        tempCommunityDatas.add(
                            BoardPagePosts(
                                type = ConstVariable.TYPE_COMMUNITY,
                                boardPostItem = a,
                                boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE)
                        )
                    }
                    tempCommunityDatas.add(
                        BoardPagePosts(
                            type = ConstVariable.TYPE_WEEK_TOP,
                            boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE)
                    )
                    for (a in safeRes.data.weekPost) {
                        tempCommunityDatas.add(
                            BoardPagePosts(
                                type = ConstVariable.TYPE_COMMUNITY,
                                boardPostItem = a,
                                boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE)
                        )
                    }
                    Pair(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_COMMUNITYPAGE),null)
            }
            is ResultWrapper.GenericError-> {
                setNoItemData(tempCommunityDatas)
                Pair(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_COMMUNITYPAGE), ErrorBody(safeRes.code,safeRes.message,safeRes.errorData))
            }
            is ResultWrapper.NetworkError -> {
                setNoItemData(tempCommunityDatas)
                Pair(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_COMMUNITYPAGE),ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
            }
        }

        boardPagePostDao.insertItemList(tempCommunityDatas)
        return Pair(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_COMMUNITYPAGE),ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
    }

    fun setNoItemData(items : ArrayList<BoardPagePosts>){
        items.add(
            BoardPagePosts(
                type = ConstVariable.TYPE_NO_LIST,
                boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE))
        items.add(
            BoardPagePosts(
            type = ConstVariable.TYPE_WEEK_TOP,
            boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE))
        items.add(
            BoardPagePosts(
                type = ConstVariable.TYPE_NO_LIST,
                boardPostShowPosition = ConstVariable.DB_COMMUNITYPAGE))
    }

    //커뮤니티 게시판 리스트
    suspend fun getCommunityBoardList(accessToken: String?,uId: String?, sortType:String?): Pair<CommunityBoardCategoryBody?, ErrorBody?> {
        val safeRes = if (accessToken == null || uId == null) {
            safeApiCall(Dispatchers.IO) { communityService.getBoardListOfGuest() }
        } else {
            safeApiCall(Dispatchers.IO) {
                if(sortType.isNullOrEmpty() || sortType == ConstVariable.SORTFANTOO) {
                    communityService.getBoardListOfMemberFantoo(accessToken, uId)
                }else{
                    communityService.getBoardListOfMemberPopular(
                        accessToken,
                        uId
                    )
                }
            }
        }
        return when (safeRes) {
            is ResultWrapper.Success -> {
                Pair(safeRes.data, null)
            }
            is ResultWrapper.GenericError -> {
                Pair(null, ErrorBody(safeRes.code, safeRes.message, safeRes.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "NetWork Error! Please Check NetWork state", null))
            }
        }
    }

    //커뮤니티 공지 리스트
    suspend fun getCommunityNoticeList() = when (val noticeRes =
        safeApiCall(Dispatchers.IO) { communityService.getCommunityTopNotices() }) {
        is ResultWrapper.Success -> {
            Pair(noticeRes.data, null)
        }
        is ResultWrapper.GenericError -> {
            Pair(null, ErrorBody(noticeRes.code, noticeRes.message, noticeRes.errorData))
        }
        is ResultWrapper.NetworkError -> {
            Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error!", null))
        }
    }


    //게시판 검색 리스트
    suspend fun getSearchResult(
        accessToken: String?,
        uId: String?,
        searchStr: String,
        nextId: Int = 0
    ): Triple<List<BoardPagePosts>?, Int?, ErrorBody?> {
        val safeRes = safeApiCall(Dispatchers.IO) {
            if (accessToken == null || uId == null) {
                communityService.callSearchCommunityPost(searchStr, nextId, 10)
            } else {
                communityService.callSearchCommunityPost(accessToken, uId, searchStr, nextId, 10)
            }
        }
        return when (safeRes) {
            is ResultWrapper.Success -> {
                if(nextId==0){
                    boardPagePostDao.deleteAllItemOfShowType(ConstVariable.BOARD_POST_MAIN_SEARCH)
                }
                val boardPageItems = arrayListOf<BoardPagePosts>()
                val resultItem = safeRes.data
                resultItem.post.forEach {
                    boardPageItems.add(
                        BoardPagePosts(
                            type = ConstVariable.TYPE_COMMUNITY,
                            boardPostItem = it,
                            boardPostShowPosition = ConstVariable.BOARD_POST_MAIN_SEARCH)
                    )
                }
                boardPagePostDao.insertItemList(boardPageItems)
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.BOARD_POST_MAIN_SEARCH), resultItem.nextId, null)
            }
            is ResultWrapper.GenericError -> {
                Triple(null, null, ErrorBody(safeRes.code, safeRes.message, safeRes.errorData))

            }
            is ResultWrapper.NetworkError -> {
                Triple(null, null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error", null))

            }
        }
    }

    suspend fun fetchCommunityCategory(
        accessToken: String,
        integUid: String
    ): ResultWrapper<CommunityBoardCategoryBody> = safeApiCall(Dispatchers.IO) {
        communityService.getBoardListOfMemberFantoo(accessToken, integUid)
    }

    suspend fun fetchCommunitySubCategory(
        accessToken: String,
        boardCode: String,
        integUid: String
    ): ResultWrapper<BoardSubCategory> = safeApiCall(Dispatchers.IO) {
        communityService.getBoardCategory(accessToken, boardCode, integUid)
    }

    suspend fun composeCommunityPost(
        accessToken: String,
        composeCommunityPost: ComposeCommunityPost,
        code: String
    ) = safeApiCall(Dispatchers.IO) {
        communityService.composeCommunityPost(accessToken, composeCommunityPost, code)
    }

    suspend fun modifyCommunityPost(
        accessToken: String,
        composeCommunityPost: ComposeCommunityPost,
        code: String,
        postId: Int
    ) = safeApiCall(Dispatchers.IO) {
        communityService.modifyCommunityPost(accessToken, composeCommunityPost, code, postId)
    }

}