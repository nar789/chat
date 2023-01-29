package com.rndeep.fns_fantoo.ui.regist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.AccessTokenResponse
import com.rndeep.fns_fantoo.data.remote.dto.AuthCodeResponse
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.data.remote.model.RegistResponse
import com.rndeep.fns_fantoo.data.remote.model.RegistResult
import com.rndeep.fns_fantoo.data.remote.dto.UserNickCheckResponse
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.ui.login.AuthRepository
import com.rndeep.fns_fantoo.ui.login.CommonRepository
import com.rndeep.fns_fantoo.ui.regist.RegistRepository
import com.rndeep.fns_fantoo.utils.ConstVariable.RESULT_SUCCESS_CODE
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AgreeConfirmViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val registRepository: RegistRepository,
    private val commonRepository: CommonRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _nickNameSetted = MutableLiveData<Boolean>()
    val nickNameSetted: LiveData<Boolean>
        get() = _nickNameSetted

    fun setNickNameSetted(set: Boolean) {
        _nickNameSetted.value = set
    }

    private var _selectedCountry = MutableLiveData<Country>()
    val selectedCountry: LiveData<Country>
        get() = _selectedCountry

    fun setSelectCountry(country: Country) {
        _selectedCountry.value = country
    }

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _checkingNickNameLiveData = MutableLiveData<UserNickCheckResponse?>()
    val checkingNickNameLiveData: MutableLiveData<UserNickCheckResponse?>
        get() = _checkingNickNameLiveData

    fun checkUserNickname(nickName: String) = viewModelScope.launch {
        try {
            when (val response = registRepository.checkUserNickname(nickName)) {
                is ResultWrapper.Success -> {
                    _checkingNickNameLiveData.value =
                        UserNickCheckResponse(RESULT_SUCCESS_CODE, "", response.data, null)
                }
                is ResultWrapper.GenericError -> {
                    _checkingNickNameLiveData.value =
                        UserNickCheckResponse(
                            response.code.toString(),
                            "",
                            null,
                            response.errorData
                        )
                }
                else -> {}
            }
        } catch (e: Exception) {
        }
    }

    private var _joinEmailLiveData = MutableLiveData<RegistResponse?>()
    val joinEmailLiveData: MutableLiveData<RegistResponse?>
        get() = _joinEmailLiveData

    fun joinByEmail(value: HashMap<String, String>) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = registRepository.joinByEmail(value)
        loadingStatusLiveData.value = false
        try {
            Timber.d("joinByEmail ret : $response")
            when (response) {
                is ResultWrapper.Success -> {
                    _joinEmailLiveData.value =
                        RegistResponse(RESULT_SUCCESS_CODE, "", RegistResult(true), null)
                }
                is ResultWrapper.GenericError -> {
                    _joinEmailLiveData.value = RegistResponse(
                        response.code.toString(),
                        response.message,
                        RegistResult(false), response.errorData
                    )
                }
                else -> {}
            }
        } catch (e: Exception) {

        }
    }

    private var _joinSnsLiveData = MutableLiveData<RegistResponse?>()
    val joinSnsLiveData: MutableLiveData<RegistResponse?>
        get() = _joinSnsLiveData

    fun joinBySns(value: HashMap<String, String>) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = registRepository.joinBySns(value)
        loadingStatusLiveData.value = false
        Timber.d("joinBySns ret : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _joinSnsLiveData.value =
                    RegistResponse(RESULT_SUCCESS_CODE, "", RegistResult(false), null)
            }
            is ResultWrapper.GenericError -> {
                _joinSnsLiveData.value = response.code?.let {
                    RegistResponse(
                        it, response.message,
                        RegistResult(false), response.errorData
                    )
                }
            }
            else -> {}
        }
    }

    private var _authCodeLiveData = MutableLiveData<AuthCodeResponse?>()
    val authCodeLiveData: LiveData<AuthCodeResponse?>
        get() = _authCodeLiveData

    fun getAuthCode(values: HashMap<String, String>) = viewModelScope.launch {
        val response = authRepository.getAuthCode(values)
        Timber.d("getEmailAuthCode : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _authCodeLiveData.value =
                    AuthCodeResponse(RESULT_SUCCESS_CODE, "", response.data, null)
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

            }
        }
    }

    private var _accessTokenLiveData = MutableLiveData<AccessTokenResponse?>()
    val accessTokenLiveData: LiveData<AccessTokenResponse?>
        get() = _accessTokenLiveData

    fun getAccessToken(values: HashMap<String, String>) = viewModelScope.launch {
        val response = authRepository.getAccessToken(values)
        Timber.d("getAccessToken $response")
        when (response) {
            is ResultWrapper.Success -> {
                _accessTokenLiveData.value =
                    AccessTokenResponse(RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError -> {
                _accessTokenLiveData.value = AccessTokenResponse(
                    response.code,
                    response.message, null
                )
            }
            is ResultWrapper.NetworkError -> {

            }
        }
    }

    fun setAccessToken(accessToken: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_ACCESS_TOKEN, accessToken)
        }
    }

    fun setUID(uid: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_UID, uid)
        }
    }

    fun setIsLogined(value: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.putBoolean(DataStoreKey.PREF_KEY_IS_LOGINED, value)
        }
    }

    fun setLastLoginType(loginType: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_LAST_LOGIN_TYPE, loginType)
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

    fun insertMyProfile(myProfile: MyProfile) {
        viewModelScope.launch {
            commonRepository.insertMyProfile(myProfile)
        }
    }

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }
}