package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubAllInfoResponse
import com.rndeep.fns_fantoo.data.remote.dto.ClubInterestCategoryResponse
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubCategoryViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) :
    ViewModel() {

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    var _currentCategoryCode = MutableLiveData<String>()
    val currentCategoryCode: LiveData<String>
        get() = _currentCategoryCode

    private var _categoryListLiveData = MutableLiveData<ClubInterestCategoryResponse>()
    val categoryListLiveData: LiveData<ClubInterestCategoryResponse>
        get() = _categoryListLiveData

    fun getInterestCategoryList() = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = clubSettingRepository.getClubInterestCategory()
        loadingStatusLiveData.value = false
        when (response) {
            is ResultWrapper.Success -> {
                _categoryListLiveData.value = ClubInterestCategoryResponse(
                    ConstVariable.RESULT_SUCCESS_CODE,
                    "",
                    response.data,
                    null
                )
            }
            is ResultWrapper.GenericError -> {
                _categoryListLiveData.value = ClubInterestCategoryResponse(
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

    private var _saveCategoryResultLiveData = SingleLiveEvent<ClubAllInfoResponse>()
    val saveCategoryResultLiveData: LiveData<ClubAllInfoResponse>
        get() = _saveCategoryResultLiveData

    fun saveCategory(clubId: String, requestData: HashMap<String, Any>) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = clubSettingRepository.setClubAllInfo(clubId, requestData)
        loadingStatusLiveData.value = false
        when (response) {
            is ResultWrapper.Success -> {
                _saveCategoryResultLiveData.value =
                    ClubAllInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError -> {
                _saveCategoryResultLiveData.value = ClubAllInfoResponse(
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
}