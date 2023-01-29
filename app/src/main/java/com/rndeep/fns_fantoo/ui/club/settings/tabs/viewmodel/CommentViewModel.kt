package com.rndeep.fns_fantoo.ui.club.settings.tabs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubWithDrawMemberInfoWithMeta
import com.rndeep.fns_fantoo.data.remote.model.CommentItem
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository): ViewModel(){

    fun getStorageReplyList(clubId:String, uid: String, memberId:String) : Flow<PagingData<ClubStorageReplyListWithMeta>> {
        return clubSettingRepository.getStorageReplyList(clubId, uid, memberId).cachedIn(viewModelScope)
    }
}