package com.rndeep.fns_fantoo.ui.club.settings.tabs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SaveViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository): ViewModel() {

    fun getStorageBookmarkList(clubId:String, uid:String, memberId:String) : Flow<PagingData<ClubStoragePostListWithMeta>> {
        return clubSettingRepository.getStorageBookmarkList(clubId, uid, memberId).cachedIn(viewModelScope)
    }
}