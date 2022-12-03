package com.rndeep.fns_fantoo.ui.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.AccessTokenResponse
import com.rndeep.fns_fantoo.data.remote.dto.ConfigResponse
import com.rndeep.fns_fantoo.data.remote.model.toCountryEntity
import com.rndeep.fns_fantoo.data.remote.model.toLanguageEntity
import com.rndeep.fns_fantoo.ui.common.LanguageRepository
import com.rndeep.fns_fantoo.ui.login.AuthRepository
import com.rndeep.fns_fantoo.ui.login.CommonRepository
import com.rndeep.fns_fantoo.utils.ConstVariable.Config.Companion.VALUE_DEVICE_TYPE
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.utils.ApiUrlInterceptor
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginSplashViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val authRepository: AuthRepository,
    private val commonRepository: CommonRepository,
    private val languageRepository: LanguageRepository
) :
    ViewModel() {

    var isReceivedAccessTokenResponse = MutableLiveData(false)
    private var _accessTokenLiveData = MutableLiveData<AccessTokenResponse?>()
    val accessTokenLiveData: LiveData<AccessTokenResponse?>
        get() = _accessTokenLiveData

    fun getAccessToken(values: HashMap<String, String>) = viewModelScope.launch {
        val response = authRepository.getAccessToken(values)
        Timber.d("getAccessToken $response")
        when (response) {
            is ResultWrapper.Success -> {
                _accessTokenLiveData.value =
                    AccessTokenResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
            }
            is ResultWrapper.GenericError -> {
                _accessTokenLiveData.value =
                    AccessTokenResponse(response.code.toString(), response.message, null)
            }
            is ResultWrapper.NetworkError -> {
            }
        }
        isReceivedAccessTokenResponse.value = true
    }

    var configNetworkErrorLiveData = MutableLiveData(false)
    private var _configLiveData = MutableLiveData<ConfigResponse?>()
    val configLiveData: LiveData<ConfigResponse?>
        get() = _configLiveData

    fun getConfig(serviceType: String) = viewModelScope.launch {
        val response = commonRepository.getConfig(serviceType, VALUE_DEVICE_TYPE)
        Timber.d("getConfig , $response")
        when (response) {
            is ResultWrapper.Success -> {
                _configLiveData.value = response.data
            }
            is ResultWrapper.NetworkError -> {
                configNetworkErrorLiveData.value = true
            }
            is ResultWrapper.GenericError -> {

            }
        }
    }


    fun setAccessToken(accessToken: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_ACCESS_TOKEN, accessToken)
        }
    }

    suspend fun getIsPermissionChecked(): Boolean? {
        return dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_PERMISSION_CHECKED)
    }

    /*private var _languageList = MutableLiveData<List<Language>>()
    val languageList: MutableLiveData<List<Language>>
        get() = _languageList*/
    suspend fun getSupportLanguageAll() = viewModelScope.launch {
        val response = languageRepository.getSupportLanguageAll()
        Timber.d("getSupportLanguageAll $response")
        try {
            when (response) {
                is ResultWrapper.Success -> {
                    val langList = response.data
                    for (language in langList) {
                        languageRepository.insertOrUpdateLanguage(language.toLanguageEntity())
                    }
                }
                else -> {}
            }
        } catch (e: Exception) {
            Timber.e("getSupportLanguageAll err : ${e.message}")
        }
    }

    /*private var _countryAllList = MutableLiveData<List<Country>>()
    val countryAllList: MutableLiveData<List<Country>>
        get() = _countryAllList*/
    suspend fun getCountryAll() = viewModelScope.launch {
        val response = languageRepository.getCountryAll()
        Timber.d("getCountryAll $response")
        when (response) {
            is ResultWrapper.Success -> {
                val countryList = response.data
                for (country in countryList) {
                    languageRepository.insertOrUpdateCountry(country.toCountryEntity())
                }
            }
            else -> {}
        }
    }

    suspend fun deleteAllCountryDB() {
        languageRepository.deleteAllCountryDB()
    }

    suspend fun deleteAllLanguageDB() {
        languageRepository.deleteAllLanguageDB()
    }

    suspend fun getAccessToken(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
    }

    fun setRefreshToken(refreshToken: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_REFRESH_ACCESS_TOKEN, refreshToken)
        }
    }

    suspend fun getRefreshToken(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_REFRESH_ACCESS_TOKEN)
    }

    suspend fun getRefreshTokenPublishTime(): Long? {
        return dataStoreRepository.getLong(DataStoreKey.PREF_KEY_REFRESH_TOKEN_PUBLISH_TIME)
    }

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }

    fun setLanguageCode(languageCode: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_LANGUAGE_CODE, languageCode)
        }
    }

    fun setUID(uid: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_UID, uid)
        }
    }

    suspend fun getUID(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
    }

    fun setPermissinChecked(isPermissionChecked: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.putBoolean(
                DataStoreKey.PREF_KEY_IS_PERMISSION_CHECKED,
                isPermissionChecked
            )
        }
    }

    fun setIsLogined(value: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.putBoolean(DataStoreKey.PREF_KEY_IS_LOGINED, value)
        }
    }

    suspend fun getIsLogined(): Boolean? {
        return dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)
    }

    fun setLastLoginType(loginType: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_LAST_LOGIN_TYPE, loginType)
        }
    }

    suspend fun getLastLoginType(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LAST_LOGIN_TYPE)
    }

    fun setFCMToken(fcmToken: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_FCM_TOKEN, fcmToken)
        }
    }

    fun setApiUrl(apiUrl: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_API_URL, apiUrl)
        }
    }

    fun setCloudFlareUrl(url: String) {
        viewModelScope.launch {
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_CLOUDFLARE_URL, url)
        }
    }

    suspend fun setSystemCountryCode(countryCode: String) {
        dataStoreRepository.putString(DataStoreKey.PREF_KEY_SYSTEM_COUNTRY, countryCode)
    }

}