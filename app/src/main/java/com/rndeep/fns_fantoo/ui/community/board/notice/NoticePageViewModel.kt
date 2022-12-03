package com.rndeep.fns_fantoo.ui.community.board.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.ui.community.board.CommunityBoardRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticePageViewModel @Inject constructor(
    private val communityBoardReposit: CommunityBoardRepository,
) : ViewModel() {
    private val noticeSize = 10
    private var noticeNextId: Int = 0

    private val _errorCodeLiveData = SingleLiveEvent<String>()
    val errorCodeLiveData: LiveData<String> get() = _errorCodeLiveData

    private val _loadingStateLiveData = SingleLiveEvent<Boolean>()
    val loadingStateLiveData :LiveData<Boolean> get() = _loadingStateLiveData

    private val _commonNoticeLiveData = MutableLiveData<List<CommunityNoticeItem>>()
    val commonNoticeLiveData: LiveData<List<CommunityNoticeItem>> get() = _commonNoticeLiveData

    private var topNoticeItems : List<CommunityNoticeItem>? = listOf()
    private var commonNoticeItems :List<CommunityNoticeItem> = listOf()

    fun getBoardNoticeItem(boardId: String?) {
        viewModelScope.launch {
            if(noticeNextId==-1) return@launch
            _loadingStateLiveData.value=true
            val noticeResponse :Triple<List<CommunityNoticeItem>?,Int?, ErrorBody?>
            if (boardId == null || boardId == "") {
                topNoticeItems = communityBoardReposit.getCommonTopNoticeItem()
                noticeResponse= communityBoardReposit.getCommonNoticeItem(noticeNextId, noticeSize)
            } else {
                topNoticeItems =communityBoardReposit.getBoardTopNoticeItem(boardId).first
                noticeResponse= communityBoardReposit.getBoardNoticeItem(noticeNextId, noticeSize, boardId)
            }
            if (noticeResponse.third != null) {
                _errorCodeLiveData.value =
                    noticeResponse.third!!.code ?: ConstVariable.ERROR_WAIT_FOR_SECOND
            } else if (noticeResponse.first != null) {
                commonNoticeItems=commonNoticeItems+noticeResponse.first!!
                if(topNoticeItems==null){
                    _commonNoticeLiveData.value =commonNoticeItems
                }else{
                    _commonNoticeLiveData.value = topNoticeItems!!+commonNoticeItems
                }
                noticeNextId = noticeResponse.second!!
            }
            _loadingStateLiveData.value=false
        }
    }

}