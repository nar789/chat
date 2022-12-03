package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoWithMeta
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubMemberCountDto
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubAllMembersViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) :
    ViewModel() {

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    fun getMembersList(clubId:String, uid: String) : Flow<PagingData<ClubMemberInfoWithMeta>> {
        return clubSettingRepository.getClubMemberList(clubId, uid).cachedIn(viewModelScope)
    }

    fun getSearchMembersList(clubId: String, uid: String, keyword: String) : Flow<PagingData<ClubMemberInfoWithMeta>>{
        return clubSettingRepository.getSearchMemberList(clubId, uid, keyword).cachedIn(viewModelScope)
    }

    private var _clubMemberCountLiveData = MutableLiveData<ClubMemberCountDto?>()
    val clubMemberCountLiveData : LiveData<ClubMemberCountDto?>
        get() = _clubMemberCountLiveData
    fun getMemberCount(clubId: String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.getMemberCount(clubId, uid)
        when(response){
            is ResultWrapper.Success ->{
                _clubMemberCountLiveData.value = response.data
            }
            is ResultWrapper.GenericError ->{

            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }


}