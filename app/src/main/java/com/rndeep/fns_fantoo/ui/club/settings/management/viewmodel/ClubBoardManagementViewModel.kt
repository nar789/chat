package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubCategoryResponse
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.club.settings.management.data.ArchiveData
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ClubBoardManagementViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository, private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    var currentCategoryListLiveData : List<ClubCategoryItem> = listOf()

    private var _getCategoryListResultLiveData = MutableLiveData<ClubCategoryResponse?>()
    val getCategoryListResultLiveData : LiveData<ClubCategoryResponse?>
        get() = _getCategoryListResultLiveData

    fun getSettingCategoryList(clubId:String, uid:String) = viewModelScope.launch{
        val response = clubSettingRepository.getSettingCategoryList(clubId, uid)
        when(response){
            is ResultWrapper.Success ->{
                _getCategoryListResultLiveData.value = response.data
            }
            is ResultWrapper.GenericError->{

            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _categoryListOrderSetResultLiveData = MutableLiveData<BaseResponse>()
    val categoryListOrderSetResultLiveData : LiveData<BaseResponse>
    get() = _categoryListOrderSetResultLiveData

    fun setCategoryListOrder(clubId: String, categoryCode: String, requstData:HashMap<String, Any>) = viewModelScope.launch {
        val response = clubSettingRepository.setCategoryListOrder(clubId, categoryCode, requstData)
        Timber.d("setCategoryListOrder response $response")
        when(response){
            is ResultWrapper.Success ->{
                _categoryListOrderSetResultLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError ->{
                _categoryListOrderSetResultLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }
}