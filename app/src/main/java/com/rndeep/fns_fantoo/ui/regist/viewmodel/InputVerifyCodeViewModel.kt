package com.rndeep.fns_fantoo.ui.regist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.VerifyCodeCheckResponse
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.ui.regist.RegistRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InputVerifyCodeViewModel @Inject constructor(
    private val registRepository: RegistRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: LiveData<Boolean>
        get() = _networkErrorLiveData

    private var _checkingVerifyCodeLiveData = SingleLiveEvent<VerifyCodeCheckResponse?>()
    val checkingVerifyCodeLiveData: SingleLiveEvent<VerifyCodeCheckResponse?>
        get() = _checkingVerifyCodeLiveData

    fun checkVerifyCode(verifyCode: String, email: String) = viewModelScope.launch {
        when (val response = registRepository.checkVerifyCode(verifyCode, email)) {
            is ResultWrapper.Success -> {
                _checkingVerifyCodeLiveData.value = response.data
            }
            is ResultWrapper.GenericError -> {
                _checkingVerifyCodeLiveData.value =
                    VerifyCodeCheckResponse(response.code.toString(), false)
            }
            else -> {}
        }
    }

    private var _emailVerifyCodeLiveData = SingleLiveEvent<BaseResponse?>()
    val emailVerifyCodeLiveData: SingleLiveEvent<BaseResponse?>
        get() = _emailVerifyCodeLiveData

    fun requestVerifyCodeToEmail(email: String) = viewModelScope.launch {
        when (val response = registRepository.requestVerifyCodeToEmail(email)) {
            is ResultWrapper.Success -> {
                _emailVerifyCodeLiveData.value =
                    BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", Any())
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
            is ResultWrapper.GenericError -> {
                _emailVerifyCodeLiveData.value =
                    BaseResponse(response.code!!, response.message!!, response.errorData!!)
            }
        }
    }

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }
}