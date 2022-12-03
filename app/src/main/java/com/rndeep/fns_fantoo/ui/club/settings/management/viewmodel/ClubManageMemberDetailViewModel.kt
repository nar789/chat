package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoDto
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubManageMemberDetailViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) : ViewModel(){

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private var _clubMemberInfoLiveData = MutableLiveData<ClubMemberInfoDto?>()
    val clubMemberInfoLiveData : LiveData<ClubMemberInfoDto?>
    get() = _clubMemberInfoLiveData
    fun getClubMemberInfo(clubId:String, uid:String, memberId:String) = viewModelScope.launch {
        val response = clubSettingRepository.getClubMemberInfo(clubId = clubId, uid = uid, memberId = memberId)
        when(response){
            is ResultWrapper.Success ->{
                _clubMemberInfoLiveData.value = response.data
            }
            is ResultWrapper.GenericError->{
            }
            is ResultWrapper.NetworkError->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _clubDelegateResultLiveData = MutableLiveData<BaseResponse>()
    val clubDelegateResultLiveData : LiveData<BaseResponse>
        get() = _clubDelegateResultLiveData
    fun requestClubDelegate(clubId: String, memberId: String, requestData:HashMap<String, Any>) = viewModelScope.launch {
        val response = clubSettingRepository.requestClubDelegate(clubId, memberId, requestData)
        when(response){
            is ResultWrapper.Success ->{
                _clubDelegateResultLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError->{
            }
            is ResultWrapper.NetworkError->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _clubMemberBanResultLiveData = MutableLiveData<BaseResponse?>()
    val clubMemberBanResultLiveData : LiveData<BaseResponse?>
    get() = _clubMemberBanResultLiveData
    fun requestMemberBan(clubId: String, memberId: String, requestData: HashMap<String, Any>) = viewModelScope.launch {
        val response = clubSettingRepository.requestMemberBan(clubId, memberId, requestData)
        when(response){
            is ResultWrapper.Success ->{
                _clubMemberBanResultLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError->{
                _clubMemberBanResultLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError->{
                _networkErrorLiveData.value = true
            }
        }
    }
}