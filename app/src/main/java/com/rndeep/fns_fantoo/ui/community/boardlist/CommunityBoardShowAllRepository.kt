package com.rndeep.fns_fantoo.ui.community.boardlist

import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.CommunityService
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityBoardCategoryBody
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CommunityBoardShowAllRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    @NetworkModule.ApiServer private val communityService: CommunityService,
    @NetworkModule.ApiServer private val postService: PostService
) : PostRepository(boardPagePostDao,postService) {

    suspend fun getGuestBoardAllList(): Pair<CommunityBoardCategoryBody?, ErrorBody?> {
        val res = safeApiCall(Dispatchers.IO) { communityService.getBoardListOfGuest() }
        return when (res) {
            is ResultWrapper.Success -> {
                Pair(res.data, null)
            }
            is ResultWrapper.GenericError -> {
                Pair(null, ErrorBody(res.code, res.message, res.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "NetWork Error!", null))
            }
        }
    }

    suspend fun getMemberBoarderAllList(
        accessToken: String,
        uid: String,
        sortType: String
    ): Pair<CommunityBoardCategoryBody?, ErrorBody?> {
        var safeRes: ResultWrapper<CommunityBoardCategoryBody>? = null
        when (sortType) {
            ConstVariable.SORTFANTOO -> {
                safeRes = safeApiCall(Dispatchers.IO) {
                    communityService.getBoardListOfMemberFantoo(
                        accessToken,
                        uid
                    )
                }
            }
            ConstVariable.SORTPOPULAR -> {
                safeRes = safeApiCall(Dispatchers.IO) {
                    communityService.getBoardListOfMemberPopular(
                        accessToken,
                        uid
                    )
                }
            }
        }
        safeRes?: return Pair(null,null)

        return when (safeRes) {
            is ResultWrapper.Success -> {
                Pair(safeRes.data, null)
            }
            is ResultWrapper.GenericError -> {
                Pair(null, ErrorBody(safeRes.code, safeRes.message, safeRes.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "NetWork Error!", null))
            }
        }
    }

    //Bookmark 추가
    suspend fun callRegisterBookmark(boardId: String,uid: String?,accessToken: String?): Boolean {
        val body = HashMap<String, Any>()
        if(uid == null || accessToken == null ){
            return false
        }
        body["code"] = boardId
        body["integUid"] = uid
        val bookMarkRes = safeApiCall(Dispatchers.IO) {
            communityService.registerBookmark(
                accessToken,
                body
            )
        }
        when (bookMarkRes) {
            is ResultWrapper.Success -> {
                return true
            }
            is ResultWrapper.NetworkError -> {
                return false

            }
            is ResultWrapper.GenericError -> {
                return false
            }
        }
    }

    //Bookmark 삭제
    suspend fun callUnRegisterBookmark(boardId: String,uid: String?,accessToken: String?): Boolean {
        if(uid == null || accessToken == null) return false
        val body = HashMap<String, Any>()
        body["code"] = boardId
        body["integUid"] = uid
        val bookMarkRes = safeApiCall(Dispatchers.IO) {
            communityService.deleteBookmark(
                accessToken,
                body
            )
        }
        return when (bookMarkRes) {
            is ResultWrapper.Success -> {
                true
            }
            is ResultWrapper.GenericError ->{
                false
            }
            is ResultWrapper.NetworkError -> {
                false
            }
        }
    }


}