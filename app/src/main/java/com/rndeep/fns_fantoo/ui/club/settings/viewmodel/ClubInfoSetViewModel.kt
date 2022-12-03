package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.Country
import com.rndeep.fns_fantoo.data.local.model.Language
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubAllInfoResponse
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubInfoSetViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository): ViewModel() {

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private var _clubAllInfoLiveData = MutableLiveData<ClubAllInfoResponse>()
    val clubAllInfoLiveData : LiveData<ClubAllInfoResponse>
        get() = _clubAllInfoLiveData
    fun getClubAllInfo(clubId:String, uid:String) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = clubSettingRepository.getClubAllInfo(clubId, uid)
        loadingStatusLiveData.value = false
        when(response){
            is ResultWrapper.Success ->{
                _clubAllInfoLiveData.value = ClubAllInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError->{
                _clubAllInfoLiveData.value = ClubAllInfoResponse(response.code?:"", response.message?:"", null, response.errorData)
            }
            is ResultWrapper.NetworkError->{
                _networkErrorLiveData.value = true
            }
        }
    }

    fun setClubAllInfo(clubId: String, reqData: HashMap<String, Any>) = viewModelScope.launch {
        when(val response = clubSettingRepository.setClubAllInfo(clubId, reqData)){
            is ResultWrapper.Success ->{
                _clubAllInfoLiveData.value = ClubAllInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError->{
                _clubAllInfoLiveData.value = ClubAllInfoResponse(response.code?:"", response.message?:"", null, response.errorData)
            }
            is ResultWrapper.NetworkError->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _languageLiveData = MutableLiveData<Language?>()
    val languageLiveData : LiveData<Language?>
                    get() = _languageLiveData

    fun getLanguage(langCode:String) = viewModelScope.launch{
        _languageLiveData.value = clubSettingRepository.getLanguage(langCode)
    }

    private var _countryLiveData = MutableLiveData<Country?>()
    val countryLiveData : LiveData<Country?>
    get() = _countryLiveData
    fun getCountry(iso2:String) = viewModelScope.launch {
        _countryLiveData.value = clubSettingRepository.getCountryByIso2(iso2)
    }

}