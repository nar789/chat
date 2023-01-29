package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoResponse
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageCountDto
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ClubSettingMainViewModel @Inject constructor(
    private val clubSettingRepository: ClubSettingRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private var _clubBasicInfoLiveData = MutableLiveData<BaseResponse?>()
    val clubBasicInfoLiveData: LiveData<BaseResponse?>
        get() = _clubBasicInfoLiveData

    fun getClubBasicInfo(clubId:String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.getClubBasicInfo(clubId, uid)
        try {
            when (response) {
                is ResultWrapper.Success -> {
                    _clubBasicInfoLiveData.value =
                        BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
                }
                is ResultWrapper.GenericError -> {
                    _clubBasicInfoLiveData.value =
                        BaseResponse(response.code!!, response.message!!, response.errorData!!)
                }
                is ResultWrapper.NetworkError -> {
                    _networkErrorLiveData.value = true
                }
            }
        }catch (e:Exception){
            Timber.e("${e.printStackTrace()}")
        }
    }

    private var _clubMemberInfoLiveData = MutableLiveData<ClubMemberInfoResponse>()
    val clubMemberInfoLiveData: LiveData<ClubMemberInfoResponse>
        get() = _clubMemberInfoLiveData

    fun getClubMemberInfo(clubId: String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.getClubMemberInfo(clubId, "0", uid)
        when (response) {
            is ResultWrapper.Success -> {
                _clubMemberInfoLiveData.value =
                    ClubMemberInfoResponse(
                        ConstVariable.RESULT_SUCCESS_CODE,
                        "",
                        response.data,
                        null
                    )
            }
            is ResultWrapper.GenericError -> {
                _clubMemberInfoLiveData.value = ClubMemberInfoResponse(
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

    private var _clubMyStorageInfoLiveData = MutableLiveData<ClubStorageCountDto?>()
    val clubMyStorageInfoLiveData: LiveData<ClubStorageCountDto?>
        get() = _clubMyStorageInfoLiveData

    fun getClubStorageCount(clubId: String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.getClubStorageCount(clubId, uid)
        when (response) {
            is ResultWrapper.Success -> {
                _clubMyStorageInfoLiveData.value = response.data
            }
            is ResultWrapper.GenericError -> {

            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private val _clubAlarmSetLiveData = SingleLiveEvent<BaseResponse>()
    val clubAlarmSetLiveData: SingleLiveEvent<BaseResponse>
        get() = _clubAlarmSetLiveData

    fun setClubAlarm(requestData: HashMap<String, String>, clubId: String) = viewModelScope.launch {
        when (val response = clubSettingRepository.setClubAlarm(requestData, clubId)) {
            is ResultWrapper.Success -> {
                _clubAlarmSetLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError -> {
                _clubAlarmSetLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _clubAlarmGetStateLiveData = MutableLiveData<BaseResponse>()
    val clubAlarmGetStateLiveData : LiveData<BaseResponse>
    get() = _clubAlarmGetStateLiveData

    fun getAlarmState(clubId: String, uid: String) = viewModelScope.launch {
        when(val response = clubSettingRepository.getAlarmState(clubId, uid)){
            is ResultWrapper.Success->{
                _clubAlarmGetStateLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError -> {
                _clubAlarmGetStateLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    suspend fun getUID(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
    }
}