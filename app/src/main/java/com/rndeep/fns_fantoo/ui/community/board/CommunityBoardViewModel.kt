package com.rndeep.fns_fantoo.ui.community.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.model.community.BoardSubCategory
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityBoardViewModel @Inject constructor(
    private val communityBoardReposit: CommunityBoardRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var accessToken: String? = null
    var uId: String? = null
    private var isUser =false
    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isUser=it
                if(it){
                    accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
                    uId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
                }
            }
        }
    }

    private val _globalYnLiveData = MutableLiveData<Boolean>(true)
    val globalYnLiveData: LiveData<Boolean> get() = _globalYnLiveData
    fun setGlobalState(globalState: Boolean) {
        _globalYnLiveData.value = globalState
    }

    private val _errorSnackBarMessageLiveData = SingleLiveEvent<String>()
    val errorSnackBarMessageLiveData: LiveData<String> get() = _errorSnackBarMessageLiveData

    private val _loadingStateLiveData = SingleLiveEvent<Boolean>()
    val loadingStateLiveData: LiveData<Boolean> get() = _loadingStateLiveData
    fun changeLoadingState(loadingState: Boolean) {
        _loadingStateLiveData.value = loadingState
    }

    val boardPostDatas = MutableLiveData<List<BoardPagePosts>>()
    val boardNoticeItems = MutableLiveData<List<CommunityNoticeItem>>()
    val boardCategoryItems = MutableLiveData<List<CategoryBoardCategoryList>>()

    //공지 카테고리 게시글 등 초기 셋팅 아이템
    fun getBoardPostItem(boardId: String, boardSubCode: String, postListSize: Int) {
        currentNextId = 0
        viewModelScope.launch {
            _loadingStateLiveData.value = true
            //공지
            callNoticeData(boardId)
            //카테고리
            callCategoryData(boardId)
            //포스트 아이템
            callBoardPostItem(boardId, boardSubCode, postListSize)
        }
    }

    private val _noMorePostItem = SingleLiveEvent<Boolean>()
    val noMorePostItem: LiveData<Boolean> get() = _noMorePostItem

    //게시글 아이템
    fun callBoardPostItem(boardId: String, boardSubCode: String, listSize: Int) {
        viewModelScope.launch {
            //currentNextId == -1 => 다은 게시글 존재하지 않음
            if (currentNextId == -1) {
                _noMorePostItem.value = true
                _loadingStateLiveData.value = false
                return@launch
            }
            //같은 카테고리일 경우
            //listSize => 추가 게시글 받아올 경우 10 , 현재 게시글 정보 업데이트일 경우 현재 게시글 리스트 사이즈
            val itemSize: Int = if (currentBoardSubCode == boardSubCode) {
                listSize
            } else {
                10
            }
            val boardPostItem = communityBoardReposit.getBoardPostItem(
                if(isUser) accessToken else null,
                if(isUser) uId else null,
                boardId,
                currentNextId,
                boardSubCode,
                itemSize,
                globalYnLiveData.value == true
            )

            currentBoardSubCode = boardSubCode
            if (boardPostItem.third != null) {
                boardPostItem.third!!.code?.let { _errorSnackBarMessageLiveData.value = it }
            }
            if (boardPostItem.first != null) {
                if (currentNextId != 0 && boardPostItem.second == -1) {
                    _noMorePostItem.value = true
                }
                boardPostDatas.value = boardPostItem.first!!
            }
            currentNextId = boardPostItem.second ?: 0
            _loadingStateLiveData.value = false
        }
    }

    //공지 아이템
    private suspend fun callNoticeData(boardId: String) {
        //공지 정보
        val noticeRes = communityBoardReposit.getBoardTopNoticeItem(boardId)
        if (noticeRes.second != null) {
            //Error
        } else if (noticeRes.first != null) {
            boardNoticeItems.value = noticeRes.first!!
        }
    }

    //카테고리 아이템
    private val _categoryInfoLiveData = MutableLiveData<Pair<CategoryBoardCategoryList, String?>>()
    val categoryInfoLiveData: LiveData<Pair<CategoryBoardCategoryList, String?>> get() = _categoryInfoLiveData
    private suspend fun callCategoryData(boardId: String) {
        val subCategoryRes: Pair<BoardSubCategory?, ErrorBody?> =
            communityBoardReposit.getBoardCategoryItem(
                if (isUser) accessToken else null,
                boardId, if (isUser) uId else null,
            )
        if (subCategoryRes.second != null) {
            //Error
        } else if (subCategoryRes.first != null) {
            val categoryItem = subCategoryRes.first!!
            val subCategoryList = arrayListOf<CategoryBoardCategoryList>()

            if (categoryItem.subCategoryList.isNotEmpty()) subCategoryList.add(
                CategoryBoardCategoryList(
                    "",
                    "All",
                    "전체",
                    false,
                    "Uid",
                    categoryItem.categoryData.code,
                    2,
                    1
                )
            )
            for (a in categoryItem.subCategoryList) {
                subCategoryList.add(a)
            }
            _categoryInfoLiveData.value = Pair(
                categoryItem.categoryData,
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
            )
            boardCategoryItems.value = subCategoryList
        }
    }

    //북마크 등록 및 삭제
    private val _bookmarkResultLiveData = MutableLiveData<Boolean>()
    val bookmarkResultLiveData: LiveData<Boolean> get() = _bookmarkResultLiveData
    fun bookmarkChange(isFavorite: Boolean, boardId: String) {
        viewModelScope.launch {
            if (!isUser) {
                _errorSnackBarMessageLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            val res = if (isFavorite) {
                communityBoardReposit.deleteBookmarkBoard(accessToken!!, boardId, uId!!)
            } else {
                communityBoardReposit.registerBookmarkBoard(accessToken!!, boardId, uId!!)
            }
            if (res.second != null) {
                when (res.second!!.code) {
                    ConstVariable.ERROR_AUTH -> {
                        _errorSnackBarMessageLiveData.value = ConstVariable.ERROR_AUTH
                    }
                    else -> {
                        _errorSnackBarMessageLiveData.value = ConstVariable.ERROR_LIKE_CLICK
                    }
                }

            }
            _bookmarkResultLiveData.value = res.first!!
        }
    }

    //좋아요 싫어요 선택
    private val _lickDislikeResultLiveData = MutableLiveData<List<BoardPagePosts>>()
    val likeDisLikeResultLiveData: LiveData<List<BoardPagePosts>> get() = _lickDislikeResultLiveData
    fun requestPostLike(dbId: Int, likeType: String, postID: String, isCancel: Boolean) {
        viewModelScope.launch {
            if (!isUser) {
                _errorSnackBarMessageLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            } else {
                communityBoardReposit.updatePostLike(
                    dbId,
                    accessToken!!,
                    isCancel,
                    uId!!,
                    likeType,
                    postID
                ).run {
                    if (this.second != null) {
                        _errorSnackBarMessageLiveData.value =
                            this.second!!.code ?: ConstVariable.ERROR_WAIT_FOR_SECOND
                    } else {
                        _lickDislikeResultLiveData.value = this.first!!
                    }
                }
            }
        }
    }

    //Honor 선택
    suspend fun requestHonor(dbId: Int): List<BoardPagePosts>? {
        return communityBoardReposit.updatePostHonor(dbId)
    }

    private val _boardPieceBlockResultLiveData = SingleLiveEvent<Int>()
    val boardPieceBlockLiveData: LiveData<Int> get() = _boardPieceBlockResultLiveData
    fun pieceBlockPost(
        targetUid: String,
        targetType: String,
        targetPostId: String,
        isBlock: Boolean
    ) {
        viewModelScope.launch {
            if (!isUser) {
                _errorSnackBarMessageLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            communityBoardReposit.blockPiecePost(
                accessToken!!,
                uId!!,
                targetUid,
                targetType,
                targetPostId,
                isBlock
            ).run {
                _boardPieceBlockResultLiveData.value = this
            }
        }
    }

    private val _boardUserBlockResultLiveData = SingleLiveEvent<String>()
    val boardUserBlockLiveData: LiveData<String> get() = _boardUserBlockResultLiveData
    fun userBlockPost(targetUid: String, isBlock: Boolean) {
        viewModelScope.launch {
            if (!isUser) {
                _errorSnackBarMessageLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            communityBoardReposit.blockUserPost(accessToken!!, uId!!, targetUid, isBlock).run {
                _boardPieceBlockResultLiveData.value = this
            }
        }
    }

    //게시글 삭제
    private val _boardDeletePostResultLiveData = MutableLiveData<List<BoardPagePosts>>()
    val boardDeletePostResultLiveData: LiveData<List<BoardPagePosts>> get() = _boardDeletePostResultLiveData
    fun callDeletePost(code: String, postId: String, dbId: Int) {
        viewModelScope.launch {
            if (!isUser) {
                _errorSnackBarMessageLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            communityBoardReposit.updateDeletePost(accessToken!!, uId!!, code, postId, dbId).run {
                if (this.second != null) {
                    _errorSnackBarMessageLiveData.value = this.second!!.toString()
                } else {
                    _boardDeletePostResultLiveData.value = this.first!!
                }
            }
        }
    }

    fun isMember() = isUser

    //페이징 처리를 위한 NextId 변수 저장
    var currentNextId = 0
    fun setInitCurrentNextId() {
        currentNextId = 0
    }

    //현재 선택된 subCategory 변수 저장
    private var currentBoardSubCode = ""
    fun getCurrentBoardCategoryCode() = this.currentBoardSubCode


}