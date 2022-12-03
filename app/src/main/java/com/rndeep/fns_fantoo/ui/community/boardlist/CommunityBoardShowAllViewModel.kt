package com.rndeep.fns_fantoo.ui.community.boardlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityBoardCategoryBody
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommunityBoardShowAllViewModel @Inject constructor(
    private val repository: CommunityBoardShowAllRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var accessToken: String? = null
    private var uId: String? = null
    private var isUser = false

    private val _boardListFavoriteStateLiveData = MutableLiveData<Boolean>()
    val boardListFavoriteStateLiveData: LiveData<Boolean> get() = _boardListFavoriteStateLiveData

    init {
        viewModelScope.launch {
            _boardListFavoriteStateLiveData.value =
                (dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_BOARD_LIST_FAVORITE_STATE) == true)
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isUser=it
                if(it){
                    accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
                    uId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
                }
            }
        }
    }

    fun setFavoriteState(isFavorite: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.putBoolean(
                DataStoreKey.PREF_KEY_BOARD_LIST_FAVORITE_STATE,
                isFavorite
            )
            _boardListFavoriteStateLiveData.value = isFavorite
        }
    }


    private val _boardAllListItemLiveData = MutableLiveData<List<CategoryBoardCategoryList>>()
    val boardAllCategoryListItemLiveData: LiveData<List<CategoryBoardCategoryList>> get() = _boardAllListItemLiveData

    fun settingFavoriteItem(type: String) {
        viewModelScope.launch {
            var boardCategoryData: Pair<CommunityBoardCategoryBody?, ErrorBody?>? = null
            boardCategoryData =
                if (!isUser) {
                    repository.getGuestBoardAllList()
                } else {
                    repository.getMemberBoarderAllList(accessToken!!, uId!!, type)
                }
            boardCategoryData ?: return@launch
            if (boardCategoryData.second != null) {
                // code == 666 => Network Error
            } else if (boardCategoryData.first != null) {
                _boardAllListItemLiveData.value = boardCategoryData.first!!.categoryBoardList
            }
        }
    }

    val registerBookmarkLiveData = SingleLiveEvent<Boolean>()
    fun registerBookmark(boardId: String) {
        viewModelScope.launch {
            registerBookmarkLiveData.value = repository.callRegisterBookmark(
                boardId, dataStoreRepository.getString(
                    DataStoreKey.PREF_KEY_UID
                ), dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
            )
        }
    }

    fun unRegisterBookmark(boardId: String) {
        viewModelScope.launch {
            registerBookmarkLiveData.value = repository.callUnRegisterBookmark(
                boardId, dataStoreRepository.getString(
                    DataStoreKey.PREF_KEY_UID
                ), dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
            )
        }
    }

    var bottomSheetItem: ArrayList<BottomSheetItem> = arrayListOf()

    fun settingBottomSheetItem(bottomItemTitle: List<String>, bottomItemSubTitle: List<String>) {
        val bottomItems = ArrayList<BottomSheetItem>()
        for ((index, a) in bottomItemTitle.withIndex()) {
            bottomItems.add(BottomSheetItem(null, a, bottomItemSubTitle[index], index == 0))
        }
        bottomSheetItem = bottomItems
    }

    fun showBottomSheetItem(pos: Int?) {
        for ((index, a) in bottomSheetItem.withIndex()) {
            a.isChecked = index == pos
        }
        sortType = when (pos) {
            0 -> "Recommend"
            1 -> "Popular"
            else -> "Recommend"
        }
    }

    private var sortType = "Recommend"

    fun getSortType() = sortType

    fun isMember() = isUser

}