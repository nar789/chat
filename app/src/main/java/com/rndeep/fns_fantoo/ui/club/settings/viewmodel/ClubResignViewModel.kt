package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubResignViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository)
    : ViewModel(){

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private val _clubLeaveLiveData = MutableLiveData<BaseResponse>()
    val clubLeaveLiveData : LiveData<BaseResponse>
        get() = _clubLeaveLiveData

    fun leaveClub(clubId:String, uid:String) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = clubSettingRepository.leaveClub(clubId, uid)
        loadingStatusLiveData.value = false
        when(response){
            is ResultWrapper.Success ->{
                _clubLeaveLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", "")
            }
            is ResultWrapper.GenericError->{
                _clubLeaveLiveData.value = BaseResponse(response.code.toString(), response.message?:"", response.errorData?:"")
            }
            is ResultWrapper.NetworkError->{
                _networkErrorLiveData.value = true
            }
        }
    }
}