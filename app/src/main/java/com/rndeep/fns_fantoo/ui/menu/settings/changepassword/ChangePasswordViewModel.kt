package com.rndeep.fns_fantoo.ui.menu.settings.changepassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.userinfo.PasswordValidator
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val userInfoRepository: UserInfoRepository,
) : ViewModel() {

    private var _isMatchPassword = MutableLiveData<Boolean>()
    val isMatchPassword: LiveData<Boolean>
        get() = _isMatchPassword

    private var _changePasswordSuccess = MutableLiveData<Boolean>()
    val changePasswordSuccess: LiveData<Boolean>
        get() = _changePasswordSuccess

    private var newPassword: String? = null
    var errorMessage: String? = null

    private lateinit var accessToken: String
    private lateinit var integUid: String

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
        }
    }

    fun checkPasswordRegex(password: String) = password.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,20}$".toRegex())

    fun checkConfirmPassword(input: String) = input == newPassword

    fun setNewPassword(input: String) {
        newPassword = input
    }

    fun checkCurrentPassword(password: String) = viewModelScope.launch {
        val response = userInfoRepository.checkPassword(accessToken, PasswordValidator(integUid, password) )
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _isMatchPassword.value = response.data.isMatchePw
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                if(response.errorData?.error == "MethodArgumentNotValidException") {
                    errorMessage = response.errorData.message
                    _isMatchPassword.value = false
                } else {

                }
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun startChangePassword() = viewModelScope.launch {
        val response = userInfoRepository.updatePassword(accessToken, PasswordValidator(integUid, newPassword.toString()) )
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _changePasswordSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _changePasswordSuccess.value = false
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }
}