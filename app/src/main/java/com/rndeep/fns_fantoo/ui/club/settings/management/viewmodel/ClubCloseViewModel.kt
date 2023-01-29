package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

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
class ClubCloseViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository):ViewModel() {

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private var _clubCloseRequestResultLiveData = SingleLiveEvent<BaseResponse?>()
    val clubCloseRequestResultLiveData : LiveData<BaseResponse?>
        get() = _clubCloseRequestResultLiveData
    fun requestClubClose(clubId:String, requestData: HashMap<String,Any>) = viewModelScope.launch{
        val response = clubSettingRepository.requestClubClose(clubId, requestData)
        when(response){
            is ResultWrapper.Success ->{
                _clubCloseRequestResultLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError ->{
                _clubCloseRequestResultLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }
}