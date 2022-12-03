package com.rndeep.fns_fantoo.ui.club.settings.tabs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository): ViewModel() {

    fun getStoragePostList(clubId:String, uid:String, memberId:String) : Flow<PagingData<ClubStoragePostListWithMeta>> {
        return clubSettingRepository.getStoragePostList(clubId, uid, memberId).cachedIn(viewModelScope)
    }
}