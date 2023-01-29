package com.rndeep.fns_fantoo.ui.menu.settings.accountinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.ui.menu.IconType
import com.rndeep.fns_fantoo.ui.menu.LoginType
import com.rndeep.fns_fantoo.ui.menu.MenuItem
import com.rndeep.fns_fantoo.ui.menu.MenuItemType
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _accountInfoMenu = MutableLiveData<List<MenuItem>>()
    val accountInfoMenu: LiveData<List<MenuItem>>
        get() = _accountInfoMenu

    private var _unAuthorized = MutableLiveData<Boolean>()
    val unAuthorized: LiveData<Boolean>
        get() = _unAuthorized

    // Account Info
    private var account = MenuItem.AccountItem(
        MenuItemType.SIGNUP_ACCOUNT,
        null,
        LoginType.EMAIL
    )

    private var qrCode = MenuItem.Item(
        MenuItemType.QR_AUTH,
        null,
        IconType.IMAGE
    )

    private val changePassword = MenuItem.Item(
        MenuItemType.CHANGE_PASSWORD,
        null,
        IconType.IMAGE
    )

    private val logout = MenuItem.Item(
        MenuItemType.LOGOUT,
        null,
        IconType.IMAGE
    )

    private val deleteAccount = MenuItem.Item(
        MenuItemType.DELETE_ACCOUNT,
        null,
        IconType.IMAGE
    )

    private var accountInfoItems = mutableListOf<MenuItem>()

    private lateinit var accessToken: String
    private lateinit var integUid: String
    private lateinit var userInfo: UserInfoResponse

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchUserInfo()
        }
    }

    fun getIntegUid() = integUid

    fun startLogout() = viewModelScope.launch {
        val response = userInfoRepository.logout(accessToken, IntegUid(integUid))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                clearUserInfo()
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

    private fun fetchUserInfo() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(integUid))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                userInfo = response.data
                account.icon = getLoginType(userInfo.loginType)
                account.value = userInfo.loginId
                setAccountInfoItems(userInfo.loginType)
                _accountInfoMenu.postValue(accountInfoItems)
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _accountInfoMenu.postValue(accountInfoItems)
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun clearUserInfo() = viewModelScope.launch {
        dataStoreRepository.putString(DataStoreKey.PREF_KEY_REFRESH_ACCESS_TOKEN, "")
        dataStoreRepository.putString(DataStoreKey.PREF_KEY_UID, "")
        dataStoreRepository.putLong(DataStoreKey.PREF_KEY_REFRESH_TOKEN_PUBLISH_TIME, 0)
        dataStoreRepository.putBoolean(DataStoreKey.PREF_KEY_IS_LOGINED, false)
        userInfoRepository.deleteProfile()
        _unAuthorized.value = true
    }

    private fun setAccountInfoItems(loginType: String) {
        accountInfoItems = when (loginType) {
            "email" -> listOf(account, qrCode, changePassword, logout, deleteAccount).toMutableList()
            else -> listOf(account, qrCode, logout, deleteAccount).toMutableList()
        }
    }

    private fun getLoginType(type: String) = when (type) {
        "apple" -> LoginType.APPLE
        "google" -> LoginType.GOOGLE
        "facebook" -> LoginType.FACEBOOK
        "line" -> LoginType.LINE
        "kakao" -> LoginType.KAKAO
        else -> LoginType.EMAIL
    }

    companion object {
        const val UNAUTHORIZED = "401"
    }

}