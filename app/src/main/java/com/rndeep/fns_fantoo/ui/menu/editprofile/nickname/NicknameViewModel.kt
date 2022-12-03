package com.rndeep.fns_fantoo.ui.menu.editprofile.nickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.userinfo.UserInfoType
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.utils.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow<NicknameUIState>(NicknameUIState.None)
    val state = _state.asStateFlow()

    private val _currentNickname = MutableLiveData<String>()
    val currentNickname: LiveData<String> = _currentNickname

    var changedNickname: String? = null

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

    fun checkDuplicateNickname(nickname: String) = viewModelScope.launch {
        when {
            nickname.isEmpty() -> {
                _state.value = NicknameUIState.Success(NicknameState.EMPTY)
            }
            else -> {
                _state.value = NicknameUIState.Loading
                requestCheckNickname(nickname)
            }
        }
    }

    private fun checkThirtyDays(): Boolean {
        val validDateTime = TimeUtils.getParsedLocalDateTime(userInfo.userNickUpdate)
            .plusDays(NICKNAME_VALID_DATE.toLong())
        val now = TimeUtils.getCurrentLocalDateTime()

        Timber.d("validDateTime : $validDateTime")
        Timber.d("          now : $now")

        return validDateTime.isAfter(now)
    }

    private fun requestCheckNickname(nickname: String) = viewModelScope.launch {
        Timber.d("nickname: $nickname")
        val response = userInfoRepository.checkNickname(nickname)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                val userNickCheck = response.data
                when {
                    checkThirtyDays() -> {
                        _state.update {
                            NicknameUIState.Success(NicknameState.THIRTY_DAYS)
                        }
                    }
                    userNickCheck.isCheck -> {
                        _state.update {
                            NicknameUIState.Success(NicknameState.DUPLICATED)
                        }
                    }
                    else -> {
                        _state.update {
                            NicknameUIState.Success(NicknameState.VALID)
                        }
                    }
                }
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _state.update {
                    NicknameUIState.Error(Throwable(response.errorData?.message))
                }
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun updateNickname() = viewModelScope.launch {
        Timber.d("updateNickname")

        changedNickname?.let { nickname ->
            userInfo.userNick = nickname
            val response = userInfoRepository.updateUserInfoByType(accessToken, userInfo, UserInfoType.USERNICK.type)
            Timber.d("responseData : $response")
            when (response) {
                is ResultWrapper.Success -> {
                    Timber.d("success")
                    updateProfileNickname(nickname)
                    _state.update {
                        NicknameUIState.Success(NicknameState.COMPLETED)
                    }
                }
                is ResultWrapper.GenericError -> {
                    Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                    _state.update {
                        NicknameUIState.Error(Throwable(response.errorData?.message))
                    }
                }
                is ResultWrapper.NetworkError -> {
                    // TODO handling network error
                    Timber.d("network error")
                }
            }
        }
    }

    private fun fetchUserInfo() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(integUid))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                userInfo = response.data
                _currentNickname.value = userInfo.userNick.toString()
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _state.update {
                    NicknameUIState.Error(Throwable(response.errorData?.message))
                }
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun updateProfileNickname(nickname: String) = viewModelScope.launch {
        userInfoRepository.updateNickname(nickname)
    }

    companion object {
        const val NICKNAME_VALID_DATE = 30
    }

}