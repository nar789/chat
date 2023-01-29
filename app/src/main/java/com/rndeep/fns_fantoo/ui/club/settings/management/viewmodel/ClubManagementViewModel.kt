package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubManageInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.club.ClubLoginResponse
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
class ClubManagementViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) :
    ViewModel() {

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private var _clubManageInfoLiveData = MutableLiveData<ClubManageInfoResponse>()
    val clubManageInfoLiveData: LiveData<ClubManageInfoResponse>
        get() = _clubManageInfoLiveData

    fun getClubInfo(clubId: String) = viewModelScope.launch {
        val response = clubSettingRepository.getClubManageInfo(clubId)
        when (response) {
            is ResultWrapper.Success -> {
                _clubManageInfoLiveData.value = ClubManageInfoResponse(
                    ConstVariable.RESULT_SUCCESS_CODE,
                    "",
                    response.data,
                    null
                )
            }
            is ResultWrapper.GenericError -> {
                _clubManageInfoLiveData.value = ClubManageInfoResponse(
                    response.code!!,
                    response.message!!,
                    null,
                    response.errorData
                )
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _clubLoginResultLiveData = MutableLiveData<ClubLoginResponse>()
    val clubLoginResultLiveData : LiveData<ClubLoginResponse>
    get() = _clubLoginResultLiveData

    fun loginClub(clubId: String, requestData : HashMap<String,Any>) = viewModelScope.launch {
        val response = clubSettingRepository.loginClub(clubId, requestData)
        when (response) {
            is ResultWrapper.Success -> {
                _clubLoginResultLiveData.value = ClubLoginResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError -> {
                _clubLoginResultLiveData.value = ClubLoginResponse(response.code!!, response.message, null, response.errorData)
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _clubCloseStateInfoLiveData = MutableLiveData<BaseResponse>()
    val clubCloseStateInfoLiveData : LiveData<BaseResponse>
    get() = _clubCloseStateInfoLiveData

    fun getClubCloseStateInfo(clubId: String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.getClubCloseStateInfo(clubId, uid)
        when(response){
            is ResultWrapper.Success ->{
                _clubCloseStateInfoLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError ->{
                _clubCloseStateInfoLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _joinWaitMemberCountLiveData = MutableLiveData<BaseResponse>()
    val joinWaitMemberCountLiveData : LiveData<BaseResponse>
        get() = _joinWaitMemberCountLiveData

    fun getJoinWaitMemberCount(clubId: String, uid: String) = viewModelScope.launch{
        val response = clubSettingRepository.getJoinWaitMemberCount(clubId, uid)
        when(response){
            is ResultWrapper.Success ->{
                _joinWaitMemberCountLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError ->{
                _joinWaitMemberCountLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

}