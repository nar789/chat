package com.rndeep.fns_fantoo.ui.menu.settings.deleteaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.MyWalletResponse
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val userInfoRepository: UserInfoRepository,
) : ViewModel() {

    private lateinit var accessToken: String
    private lateinit var integUid: String

    private var _userInfo = MutableLiveData<UserInfoResponse?>()
    val userInfo: LiveData<UserInfoResponse?> = _userInfo

    private val _myWallet = MutableLiveData<MyWalletResponse?>()
    val myWallet: LiveData<MyWalletResponse?> = _myWallet

    private var _unAuthorized = MutableLiveData<Boolean>()
    val unAuthorized: LiveData<Boolean> = _unAuthorized

    private var _isChecked = MutableLiveData<Boolean>()
    val isChecked: LiveData<Boolean> = _isChecked

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchUserInfo()
            fetchUserWallet()
            _isChecked.value = false
            _unAuthorized.value = false
        }
    }

    fun startDeleteAccount() = viewModelScope.launch {
        dataStoreRepository.putString(DataStoreKey.PREF_KEY_REFRESH_ACCESS_TOKEN, "")
        dataStoreRepository.putLong(DataStoreKey.PREF_KEY_REFRESH_TOKEN_PUBLISH_TIME, 0)
        dataStoreRepository.putBoolean(DataStoreKey.PREF_KEY_IS_LOGINED, false)
        _unAuthorized.value = true
//        userInfoRepository.startDeleteAccount()
    }

    private fun fetchUserInfo() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(integUid))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _userInfo.value = response.data
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun fetchUserWallet() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserWallet(accessToken, integUid)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _myWallet.value = response.data
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun setChecked(checked: Boolean) {
        _isChecked.value = checked
    }
}