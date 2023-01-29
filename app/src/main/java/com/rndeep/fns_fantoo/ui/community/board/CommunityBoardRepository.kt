package com.rndeep.fns_fantoo.ui.community.board

import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.CommunityService
import com.rndeep.fns_fantoo.data.remote.api.PostDetailService
import com.rndeep.fns_fantoo.data.remote.model.community.BoardSubCategory
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeDataObj
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CommunityBoardRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    @NetworkModule.ApiServer private val communityService: CommunityService,
    @NetworkModule.ApiServer private val postDetailService: PostDetailService
) : BaseNetRepo() {

    //북마크 등록
    suspend fun registerBookmarkBoard(
        accessToken: String,
        boardCode: String,
        uId: String
    ): Pair<Boolean, ErrorBody?> {
        val hashBody = HashMap<String, Any>()
        hashBody["code"] = boardCode
        hashBody["integUid"] = uId
        val res = safeApiCall(Dispatchers.IO) {
            communityService.registerBookmark(
                accessToken,
                hashBody
            )
        }
        return when (res) {
            is ResultWrapper.Success -> {
                Pair(true, null)
            }
            is ResultWrapper.GenericError -> {
                Pair(false, ErrorBody(res.code, res.message, res.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Pair(false, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error!", null))
            }
        }
    }

    //북마크 삭제
    suspend fun deleteBookmarkBoard(
        accessToken: String,
        boardCode: String,
        uId: String
    ): Pair<Boolean, ErrorBody?> {
        val hashBody = HashMap<String, Any>()
        hashBody["code"] = boardCode
        hashBody["integUid"] = uId
        val res = safeApiCall(Dispatchers.IO) {
            communityService.deleteBookmark(
                accessToken,
                hashBody
            )
        }
        return when (res) {
            is ResultWrapper.Success -> {
                Pair(true, null)
            }
            is ResultWrapper.GenericError -> {
                Pair(false, ErrorBody(res.code, res.message, res.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Pair(false, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error!", null))
            }
        }
    }


    //상단 노출 공지 data
    suspend fun getBoardTopNoticeItem(code: String): Pair<List<CommunityNoticeItem>?, ErrorBody?> {
        val res = safeApiCall(Dispatchers.IO) { communityService.getBoardNoticeTop(code) }
        return when (res) {
            is ResultWrapper.Success -> {
                Pair(res.data.notice, null)
            }
            is ResultWrapper.GenericError -> {
                Pair(null, ErrorBody(res.code, res.message, res.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error", null))
            }
        }
    }

    //카테고리 정보
    suspend fun getBoardCategoryItem(
        accessToken: String?,
        boardCode: String,
        uId: String?
    ): Pair<BoardSubCategory?, ErrorBody?> {
        var safeRes: ResultWrapper<BoardSubCategory>
        if (accessToken == null || uId == null) {
            safeRes =
                safeApiCall(Dispatchers.IO) { communityService.getGuestBoardCategory(boardCode) }
        } else {
            safeRes = safeApiCall(Dispatchers.IO) {
                communityService.getBoardCategory(accessToken, boardCode, uId)
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
                Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "NetWork Error!", null))
            }
        }
    }

    //게시글
    suspend fun getBoardPostItem(
        accessToken: String?,
        uId: String?,
        boardCode: String,
        nextId: Int,
        subCode: String,
        itemSize: Int,
        globalYn: Boolean
    ): Triple<List<BoardPagePosts>?, Int?, ErrorBody?> {
        val res = if (accessToken == null || uId == null) {
            if (subCode == "") safeApiCall(Dispatchers.IO) {
                communityService.getBoardPostGuest(
                    boardCode,
                    globalYn,
                    nextId,
                    itemSize,
                    null
                )
            }
            else safeApiCall(Dispatchers.IO) {
                communityService.getBoardPostGuest(
                    boardCode,
                    globalYn,
                    nextId,
                    itemSize,
                    subCode
                )
            }
        } else {
            if (subCode == "") safeApiCall(Dispatchers.IO) {
                communityService.getBoardPost(
                    accessToken,
                    boardCode,
                    globalYn,
                    uId,
                    nextId,
                    itemSize,
                    null
                )
            }
            else safeApiCall(Dispatchers.IO) {
                communityService.getBoardPost(
                    accessToken,
                    boardCode,
                    globalYn,
                    uId,
                    nextId,
                    itemSize,
                    subCode
                )
            }
        }

        //첫번째 게시글을 불러올 때(nextId ==0 ) 는 기존 DB를 지우고 상단 공지 타입을 미리 넣어준다.
        val insertItem = if (nextId == 0) {
            boardPagePostDao.deleteAllItemOfShowType(ConstVariable.BOARD_POST_PAGE)
            arrayListOf(
                BoardPagePosts(
                    type = ConstVariable.TYPE_COMMUNITY_NOTICE,
                    boardPostItem = null,
                    boardPostShowPosition = ConstVariable.BOARD_POST_PAGE
                )
            )
        } else {
            arrayListOf()
        }

        var errorCode: String? = null
        var errorMessage: String? = null
        var errorData: ErrorData? = null
        var receiveNextId = -1
        when (res) {
            is ResultWrapper.Success -> {
                val result = res.data
                //첫번째 게시글을 불러오지만 게시글이 없으면 N0_LIST 만 리턴
                if (result.post.isEmpty() && nextId == 0) {
                    insertItem.add(
                        BoardPagePosts(
                            type = ConstVariable.TYPE_NO_LIST,
                            boardPostItem = null,
                            boardPostShowPosition = ConstVariable.BOARD_POST_PAGE
                        )
                    )
                }
                result.post.forEach { item ->
                    insertItem.add(
                        BoardPagePosts(
                            type = ConstVariable.TYPE_COMMUNITY,
                            boardPostItem = item,
                            boardPostShowPosition = ConstVariable.BOARD_POST_PAGE
                        )
                    )
                }
                receiveNextId = result.nextId
            }
            is ResultWrapper.GenericError -> {
                if (nextId == 0) {
                    insertItem.add(
                        BoardPagePosts(
                            type = ConstVariable.TYPE_NO_LIST,
                            boardPostItem = null,
                            boardPostShowPosition = ConstVariable.BOARD_POST_PAGE
                        )
                    )
                }
                errorCode = res.code
                errorMessage = res.message
                errorData = res.errorData
            }
            is ResultWrapper.NetworkError -> {
                if (nextId == 0) {
                    insertItem.add(
                        BoardPagePosts(
                            type = ConstVariable.TYPE_NO_LIST,
                            boardPostItem = null,
                            boardPostShowPosition = ConstVariable.BOARD_POST_PAGE
                        )
                    )
                }
                errorCode = ConstVariable.ERROR_NETWORK
                errorMessage = "Network Error"
                errorData = null
            }
        }
        boardPagePostDao.insertItemList(insertItem)
        //게시글 리스트 , 페이징을 위한 nextId , errorBody 리턴
        return Triple(
            boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.BOARD_POST_PAGE),
            receiveNextId,
            ErrorBody(errorCode, errorMessage, errorData)
        )
    }

    suspend fun updatePostLike(
        dbId: Int,
        accessToken: String,
        isCancel: Boolean,
        uId: String,
        likeType: String,
        postId: String
    ): Pair<List<BoardPagePosts>?, ErrorBody?> {
        if (isCancel) {
            safeApiCall(Dispatchers.IO) {
                postDetailService.cancelPostLike(
                    accessToken,
                    ConstVariable.LIKE_DISLIKE_TYPE_POST,
                    postId,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uId
                    })
            }
        } else {
            safeApiCall(Dispatchers.IO) {
                postDetailService.clickPostCommentLike(
                    accessToken,
                    likeType,
                    ConstVariable.LIKE_DISLIKE_TYPE_POST,
                    postId,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uId
                    }
                )
            }
        }.run {
            when (this) {
                is ResultWrapper.Success -> {
                    val previewItem = boardPagePostDao.getBoardPostItem(dbId)
                    changeLikeState(likeType, previewItem)
                    previewItem.boardPostItem?.likeCnt = this.data.like
                    previewItem.boardPostItem?.dislikeCnt = this.data.dislike
                    val updatePos = boardPagePostDao.updateItem(previewItem)
                    //업데이트 성공
                    if (updatePos == 1) {
                        return Pair(
                            boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.BOARD_POST_PAGE),
                            null
                        )
                    } else {
                        //업데이트 실패
                        return Pair(
                            null,
                            ErrorBody(ConstVariable.ERROR_INTERNAL_SERVER, null, null)
                        )
                    }
                }
                is ResultWrapper.GenericError -> {
                    return Pair(null, ErrorBody(this.code, this.message, this.errorData))
                }
                is ResultWrapper.NetworkError -> {
                    return Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, null, null))
                }
            }
        }
    }

    fun changeLikeState(likeType: String, previewItem: BoardPagePosts) {
        when (likeType) {
            ConstVariable.LIKE_CLICK -> {
                if (previewItem.boardPostItem?.likeYn != null) {
                    previewItem.boardPostItem?.likeYn =
                        !previewItem.boardPostItem?.likeYn!!
                } else {
                    previewItem.boardPostItem?.likeYn = true
                }
                previewItem.boardPostItem?.dislikeYn = false
            }
            ConstVariable.DISLIKE_CLICK -> {
                if (previewItem.boardPostItem?.dislikeYn != null) {
                    previewItem.boardPostItem?.dislikeYn =
                        !previewItem.boardPostItem?.dislikeYn!!
                } else {
                    previewItem.boardPostItem?.dislikeYn = true
                }
                previewItem.boardPostItem?.likeYn = false
            }
        }
    }

    //Honor 클릭
    suspend fun updatePostHonor(dbId: Int): List<BoardPagePosts>? {
        val previewItem = boardPagePostDao.getBoardPostItem(dbId)
        if (previewItem.boardPostItem!!.honorYn != null) {
            previewItem.boardPostItem?.honorYn = !previewItem.boardPostItem?.honorYn!!
        } else {
            previewItem.boardPostItem?.honorYn = false
        }
        val updatePos = boardPagePostDao.updateItem(previewItem)
        //업데이트 성공
        if (updatePos == 1) {
            return boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.BOARD_POST_PAGE)
        } else {
            //업데이트 실패
            return null
        }
    }

    //게시글 차단
    suspend fun blockPiecePost(
        accessToken: String,
        uId: String,
        targetUid: String,
        targetType: String,
        targetId: String,
        isBlock: Boolean
    ): Int {
        safeApiCall(Dispatchers.IO) {
            if (isBlock) {
                postDetailService.unblockPiecePost(
                    accessToken,
                    targetType,
                    targetId,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uId
                        this[ConstVariable.KEY_TARGET_UID] = targetUid
                    }
                )
            } else {
                postDetailService.blockPiecePost(
                    accessToken,
                    targetType,
                    targetId,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uId
                        this[ConstVariable.KEY_TARGET_UID] = targetUid
                    }
                )
            }
        }.run {
            return when (this) {
                is ResultWrapper.Success -> {
                    200
                }
                is ResultWrapper.GenericError -> {
                    this.code?.toInt() ?: ConstVariable.ERROR_AUTH.toInt()
                }
                is ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK.toInt()
                }
            }
        }
    }

    //유저 차단
    suspend fun blockUserPost(
        accessToken: String,
        uId: String,
        targetUid: String,
        isBlock: Boolean
    ): Int {
        safeApiCall(Dispatchers.IO) {
            if (isBlock) {
                postDetailService.unblockUserPost(
                    accessToken,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uId
                        this[ConstVariable.KEY_TARGET_UID] = targetUid
                    }
                )
            } else {
                postDetailService.blockUserPost(
                    accessToken,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uId
                        this[ConstVariable.KEY_TARGET_UID] = targetUid
                    }
                )
            }
        }.run {
            return when (this) {
                is ResultWrapper.Success -> {
                    200
                }
                is ResultWrapper.GenericError -> {
                    this.code?.toInt() ?: ConstVariable.ERROR_AUTH.toInt()
                }
                is ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK.toInt()
                }
            }
        }
    }

    //게시글 삭제
    suspend fun updateDeletePost(
        accessToken: String, uId: String, code: String, postId: String, dbId: Int
    ): Pair<List<BoardPagePosts>?, Int?> {
        safeApiCall(Dispatchers.IO) {
            postDetailService.deleteMyPost(
                accessToken,
                code,
                postId,
                HashMap<String, Any>().apply { this[ConstVariable.KEY_UID] = uId }
            )
        }.run {
            return when (this) {
                is ResultWrapper.Success -> {
                    boardPagePostDao.getBoardPostItem(dbId).let {
                        it.boardPostItem?.let { item ->
                            item.activeStatus = 2
                        }
                    }
                    Pair(
                        boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.BOARD_POST_PAGE),
                        null
                    )
                }
                is ResultWrapper.GenericError -> {
                    Pair(null, this.code?.toInt())
                }
                is ResultWrapper.NetworkError -> {
                    Pair(null, ConstVariable.ERROR_NETWORK.toInt())
                }
            }
        }
    }

    //전체메인 공지
    suspend fun getCommonNoticeItem(
        nextId: Int?,
        size: Int
    ): Triple<List<CommunityNoticeItem>?,Int?, ErrorBody?> {
        val res = safeApiCall(Dispatchers.IO) {
            communityService.getCommonBoardNotice(
                nextId ?: 0,
                size
            )
        }
        return when (res) {
            is ResultWrapper.Success -> {
                Triple(res.data.notice,res.data.nextId, null)
            }
            is ResultWrapper.GenericError -> {
                Triple(null,null, ErrorBody(res.code, res.message, res.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Triple(null,null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error", null))
            }
        }
    }

    //전체 공지의 탑
    suspend fun getCommonTopNoticeItem() :List<CommunityNoticeItem>?{
        safeApiCall(Dispatchers.IO){
            communityService.getCommunityTopNotices()
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.notice
                }
                else ->{
                    null
                }
            }
        }
    }

    //게시판의 공지
    suspend fun getBoardNoticeItem(
        nextId: Int?,
        size: Int,
        boardCode: String
    ): Triple<List<CommunityNoticeItem>?,Int?, ErrorBody?> {
        val res = safeApiCall(Dispatchers.IO) {
            communityService.getBoardNoticeList(boardCode, nextId ?: 0, size)
        }

        return when (res) {
            is ResultWrapper.Success -> {
                Triple(res.data.notice,res.data.nextId, null)
            }
            is ResultWrapper.GenericError -> {
                Triple(null,null, ErrorBody(res.code, res.message, res.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Triple(null,null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error", null))
            }
        }
    }

}