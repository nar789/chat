package com.rndeep.fns_fantoo.ui.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.*
import com.rndeep.fns_fantoo.data.remote.dto.CheckingJoinUserResponse
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.RESULT_SUCCESS_CODE
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.login.AuthRepository
import com.rndeep.fns_fantoo.ui.login.CommonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val commonRepository: CommonRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val userInfoRepository: UserInfoRepository
) : ViewModel() {

    var socialType: String? = null
    var socialId: String? = null
    var isAleardyRegistUser: Boolean = false

    var myProfileCountryIso2: String = ""
    var myProfileNickName: String = ""

    private var _networkErrorLiveData = MutableLiveData<Boolean>()
    val networkErrorLiveData: LiveData<Boolean>
        get() = _networkErrorLiveData

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _authCodeLiveData = MutableLiveData<AuthCodeResponse?>()
    val authCodeLiveData: LiveData<AuthCodeResponse?>
        get() = _authCodeLiveData

    fun getAuthCode(values: HashMap<String, String>) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = repository.getAuthCode(values)
        loadingStatusLiveData.value = false
        //Timber.d("getAuthCode type = $values")
        Timber.d("getAuthCode type = $response")
        when (response) {
            is ResultWrapper.Success -> {
                _authCodeLiveData.value =
                    AuthCodeResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError -> {
                _authCodeLiveData.value = AuthCodeResponse(
                    response.code.toString(),
                    response.message,
                    null,
                    response.errorData
                )
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _accessTokenLiveData = MutableLiveData<AccessTokenResponse?>()
    val accessTokenLiveData: LiveData<AccessTokenResponse?>
        get() = _accessTokenLiveData

    fun getAccessToken(values: HashMap<String, String>) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = repository.getAccessToken(values)
        loadingStatusLiveData.value = false
        when (response) {
            is ResultWrapper.Success -> {
                _accessTokenLiveData.value =
                    AccessTokenResponse(RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError -> {
                _accessTokenLiveData.value =
                    AccessTokenResponse(response.code.toString(), response.message, null)
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _checkingJoinedUserLiveData = SingleLiveEvent<CheckingJoinUserResponse?>()
    val checkingJoinedUserLiveData: SingleLiveEvent<CheckingJoinUserResponse?>
        get() = _checkingJoinedUserLiveData

    fun checkingJoinedUser(loginType: String, loginId: String) = viewModelScope.launch {
        Timber.d("checkingJoinedUser type = $loginType , id = $loginId")
        when (val response = commonRepository.checkingJoinedUser(loginType, loginId)) {
            is ResultWrapper.Success -> {
                _checkingJoinedUserLiveData.value =
                    CheckingJoinUserResponse(RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError -> {
                _checkingJoinedUserLiveData.value = response.code?.let {
                    CheckingJoinUserResponse(
                        it,
                        response.message,
                        null,
                        response.errorData
                    )
                }
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _requestTempPasswordLiveData = SingleLiveEvent<RequestTempPasswordResponse?>()
    val requestTempPasswordLiveData: SingleLiveEvent<RequestTempPasswordResponse?>
        get() = _requestTempPasswordLiveData

    fun requestTempPassword(loginId: String) = viewModelScope.launch {
        val response = commonRepository.requestTempPassword(loginId)
        Timber.d("requestTempPassword : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _requestTempPasswordLiveData.value =
                    RequestTempPasswordResponse(RESULT_SUCCESS_CODE, "", null)
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
            is ResultWrapper.GenericError -> {
                _requestTempPasswordLiveData.value =
                    RequestTempPasswordResponse(response.code.toString(), "", response.errorData)
            }
        }
        //_requestTempPasswordLiveData.value =
    }

    fun setIsLogined(value: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.putBoolean(DataStoreKey.PREF_KEY_IS_LOGINED, value)
        }
    }

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }

    fun setLanguageCode(languageCode: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_LANGUAGE_CODE, languageCode)
        }
    }

    suspend fun getLastLoginType(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LAST_LOGIN_TYPE)
    }

    fun setLastLoginType(loginType: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_LAST_LOGIN_TYPE, loginType)
        }
    }

    fun setAccessToken(accessToken: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_ACCESS_TOKEN, accessToken)
        }
    }

    fun setRefreshAccessToken(refreshToken: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_REFRESH_ACCESS_TOKEN, refreshToken)
        }
    }

    fun setRefreshTokenPublishTime(time: Long) {
        viewModelScope.launch {
            dataStoreRepository.putLong(DataStoreKey.PREF_KEY_REFRESH_TOKEN_PUBLISH_TIME, time)
        }
    }

    fun setUID(uid: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_UID, uid)
        }
    }

    fun insertMyProfile(myProfile: MyProfile) {
        viewModelScope.launch {
            commonRepository.insertMyProfile(myProfile)
        }
    }

}