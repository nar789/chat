package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubCategoryResponse
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItem
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItemResponse
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ClubBoardSettingViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) : ViewModel(){

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    private var _createCategoryResultLiveData = MutableLiveData<ClubCategoryItemResponse?>()
    val createCategoryResultLiveData : LiveData<ClubCategoryItemResponse?>
    get() = _createCategoryResultLiveData

    fun createSettingCategory(clubId: String, categoryCode: String, requestData: HashMap<String,Any>) = viewModelScope.launch {
        val response = clubSettingRepository.createSettingCategory(clubId, categoryCode, requestData)
        when(response){
            is ResultWrapper.Success ->{
                _createCategoryResultLiveData.value = ClubCategoryItemResponse(ConstVariable.RESULT_SUCCESS_CODE, "",  response.data, null)
            }
            is ResultWrapper.GenericError ->{
                _createCategoryResultLiveData.value = ClubCategoryItemResponse(response.code!!, response.message!!, null, response.errorData)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _deleteSettingCategoryLiveData = SingleLiveEvent<BaseResponse>()
    val deleteSettingCategoryLiveData : SingleLiveEvent<BaseResponse>
    get() = _deleteSettingCategoryLiveData
    fun deleteSettingCategory(clubId: String, categoryCode: String, requestData: HashMap<String, Any>) = viewModelScope.launch {
        val response = clubSettingRepository.deleteSettingCategory(clubId, categoryCode, requestData)
        when(response){
            is ResultWrapper.Success->{
                _deleteSettingCategoryLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response)
            }
            is ResultWrapper.GenericError->{
                _deleteSettingCategoryLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError->{
                _networkErrorLiveData.value = true
            }
        }
    }

    var _categoryOpenYnLiveData = MutableLiveData<Boolean>()

    private var _modifyCategoryResultLiveData = MutableLiveData<BaseResponse?>()
    val modifyCategoryResultLiveData : LiveData<BaseResponse?>
    get() = _modifyCategoryResultLiveData
    fun modifySettingCategory(clubId:String, categoryCode:String, requestData: HashMap<String,Any>) = viewModelScope.launch {
        val response = clubSettingRepository.modifySettingCategory(clubId, categoryCode, requestData)
        Timber.d("modifySettingCategory $response")
        when(response){
            is ResultWrapper.Success ->{
                _modifyCategoryResultLiveData.value = BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError ->{
                _modifyCategoryResultLiveData.value = BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _boardListLiveData = MutableLiveData<ClubCategoryResponse?>()
    val boardListLiveData : LiveData<ClubCategoryResponse?>
        get() = _boardListLiveData
    fun getSettingCategoryList(clubId:String, uid:String) = viewModelScope.launch{
        val response = clubSettingRepository.getSettingCategoryList(clubId, uid)
        when(response){
            is ResultWrapper.Success ->{
                _boardListLiveData.value = response.data
            }
            is ResultWrapper.GenericError->{

            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }
}