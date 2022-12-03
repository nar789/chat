package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubJoinWaitMemberCountDto
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.UiDataSelectWrapper
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubJoinWaitViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) :
    ViewModel() {

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private var _clubJoinWaitMemberCountLiveData = MutableLiveData<ClubJoinWaitMemberCountDto?>()
    val clubJoinWaitMemberCountLiveData : LiveData<ClubJoinWaitMemberCountDto?>
        get() = _clubJoinWaitMemberCountLiveData
    fun getJoinWaitMemberCount(clubId: String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.getJoinWaitMemberCount(clubId, uid)
        when(response){
            is ResultWrapper.Success ->{
                _clubJoinWaitMemberCountLiveData.value = response.data
            }
            is ResultWrapper.GenericError ->{

            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    fun getJoinWaitMemberList(
        clubId: String,
        uid: String
    ): Flow<PagingData<UiDataSelectWrapper>> {
        return clubSettingRepository.getJoinWaitMemberList(clubId, uid).map { pagingData ->
            pagingData.map {
                UiDataSelectWrapper(
                    it,
                    false
                )
            }
        }.cachedIn(viewModelScope)
    }

    private var _joinClubApproveResultLiveData = SingleLiveEvent<BaseResponse>()
    val joinClubApproveResultLiveData : SingleLiveEvent<BaseResponse>
    get() = _joinClubApproveResultLiveData

    fun joinClubApprove(clubId: String, requestData:HashMap<String, Any>) = viewModelScope.launch {
        val response = clubSettingRepository.joinClubApprove(clubId, requestData)
        when(response){
            is ResultWrapper.Success -> {
                _joinClubApproveResultLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError -> {
                _joinClubApproveResultLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _joinClubRejectResultLiveData = SingleLiveEvent<BaseResponse>()
    val joinClubRejectResultLiveData : SingleLiveEvent<BaseResponse>
        get() = _joinClubRejectResultLiveData

    fun joinClubReject(clubId: String, requestData:HashMap<String, Any>) = viewModelScope.launch {
        val response = clubSettingRepository.joinClubReject(clubId, requestData)
        when(response){
            is ResultWrapper.Success -> {
                _joinClubRejectResultLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError -> {
                _joinClubRejectResultLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }
}