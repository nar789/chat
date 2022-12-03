package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubDelegatingInfoResponse
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubDelegatingViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) : ViewModel(){

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private var _clubDelegateCancelResultLiveData = MutableLiveData<BaseResponse>()
    val clubDelegateCancelResultLiveData : LiveData<BaseResponse>
    get() = _clubDelegateCancelResultLiveData
    fun cancelClubDelegating(clubId:String, memberId:String, requestData:HashMap<String, Any>) = viewModelScope.launch {
        when(val response = clubSettingRepository.cancelClubDelegating(clubId, memberId, requestData)){
            is ResultWrapper.Success ->{
                _clubDelegateCancelResultLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError->{
                _clubDelegateCancelResultLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _delegatingInfoLiveData = MutableLiveData<ClubDelegatingInfoResponse>()
    val delegatingInfoLiveData : LiveData<ClubDelegatingInfoResponse>
    get() = _delegatingInfoLiveData
    fun getDelegatingInfo(clubId:String, uid:String) = viewModelScope.launch {
        when(val response = clubSettingRepository.getClubDelegatingInfo(clubId, uid)){
            is ResultWrapper.Success ->{
                _delegatingInfoLiveData.value = ClubDelegatingInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError ->{
                _delegatingInfoLiveData.value = ClubDelegatingInfoResponse(response.code, response.message, null, response.errorData)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }
}