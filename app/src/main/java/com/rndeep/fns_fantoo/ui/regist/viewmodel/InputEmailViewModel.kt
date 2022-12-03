package com.rndeep.fns_fantoo.ui.regist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.CheckingJoinUserResponse
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.ui.login.CommonRepository
import com.rndeep.fns_fantoo.ui.regist.RegistRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InputEmailViewModel @Inject constructor(private val registRepository: RegistRepository,
                                              private val commonRepository: CommonRepository,
                                              private val dataStoreRepository: DataStoreRepository
) : ViewModel(){

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData : LiveData<Boolean>
        get() = _networkErrorLiveData

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _emailVerifyCodeLiveData = SingleLiveEvent<Boolean>()
    val emailVerifyCodeLiveData : SingleLiveEvent<Boolean>
        get() = _emailVerifyCodeLiveData

    fun requestVerifyCodeToEmail(email: String) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = registRepository.requestVerifyCodeToEmail(email)
        Timber.d("requestVerifyCodeToEmail response : $response ")
        loadingStatusLiveData.value = false
        when(response){
            is ResultWrapper.Success ->{
                _emailVerifyCodeLiveData.value = true
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
            is ResultWrapper.GenericError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _checkingJoinedUserLiveData = SingleLiveEvent<CheckingJoinUserResponse?>()
    val checkingJoinedUserLiveData : SingleLiveEvent<CheckingJoinUserResponse?>
        get() = _checkingJoinedUserLiveData

    fun checkingJoinedUser(loginType:String, loginId:String) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = commonRepository.checkingJoinedUser(loginType, loginId)
        Timber.d("checkingJoinedUser : $response")
        loadingStatusLiveData.value = false
        when(response){
            is ResultWrapper.Success ->{
                _checkingJoinedUserLiveData.value = CheckingJoinUserResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
            is ResultWrapper.GenericError ->{
                _checkingJoinedUserLiveData.value = response.code?.let {
                    CheckingJoinUserResponse(
                        it,
                        response.message,
                        null,
                        response.errorData
                    )
                }
            }
        }
    }

    suspend fun getLanguageCode():String?{
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }
}
