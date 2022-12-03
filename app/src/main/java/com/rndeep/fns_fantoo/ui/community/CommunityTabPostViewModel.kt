package com.rndeep.fns_fantoo.ui.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityBoardCategoryBody
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeDataObj
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.post.CommonPostViewModel
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityTabPostViewModel @Inject constructor(
    private val repository: CommunityRepository,
    private val dataStoreRepository: DataStoreRepository,
    ) : CommonPostViewModel(repository,dataStoreRepository) {
    //에러 상태
    private val _errorResultLiveData = SingleLiveEvent<String?>()
    val errorResultLiveData :LiveData<String?> get() = _errorResultLiveData

    private val _loadingStateLiveData = SingleLiveEvent<Boolean>()
    val loadingStateLiveData: LiveData<Boolean> get() = _loadingStateLiveData
    fun changeLoadingState(loadingState: Boolean) {
        _loadingStateLiveData.value = loadingState
    }

    //커뮤니티 공지 아이템
    private val _communityNoticeItemLiveData = MutableLiveData<List<CommunityNoticeItem>>()
    val communityNoticeItemLiveData :LiveData<List<CommunityNoticeItem>> get() = _communityNoticeItemLiveData
    //커뮤니티 상단 게시판 아이템
    val communityBoardItems = MutableLiveData<CommunityBoardCategoryBody>()
    //커뮤니티 전체 아이템
    val communityTotalItems = MutableLiveData<List<BoardPagePosts>>()

    fun getInitItem(){
        viewModelScope.launch {
            val boardRes =if(isUser()){
                repository.getCommunityBoardList(accessToken,uId)
            }else{
                repository.getCommunityBoardList(null,null)
            }
            setBoardList(boardRes)
            val noticeRes = repository.getCommunityNoticeList()
            if(noticeRes.second!=null){
                //Error 발생
            }else if(noticeRes.first!=null){
                setNoticeList(noticeRes.first!!)
            }
            if(isUser()){
                repository.getCommunityRealTimeItem(accessToken, uId)
            }else{
                repository.getCommunityRealTimeItem(null, null)
            }.run {
                if(this.second!=null){
                    communityTotalItems.value=this.first!!
                }else{
                    communityTotalItems.value=this.first!!
                }
            }

        }
    }

    private fun setBoardList(res : Pair<CommunityBoardCategoryBody?,ErrorBody?>){
        if(res.second!=null){
            //Error 처리
        }else if(res.first!=null){
            communityBoardItems.value= res.first!!
        }
    }

    private fun setNoticeList(res : CommunityNoticeDataObj){
        _communityNoticeItemLiveData.value=res.notice

    }

}