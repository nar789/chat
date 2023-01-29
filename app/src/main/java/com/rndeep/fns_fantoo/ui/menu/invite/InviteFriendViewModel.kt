package com.rndeep.fns_fantoo.ui.menu.invite

import android.content.ClipData
import android.content.ClipboardManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
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
class InviteFriendViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val userInfoRepository: UserInfoRepository,
    private val clipboardManager: ClipboardManager
) : ViewModel() {

    private var myRecommendCode: String? = null

    private val _codeState = MutableStateFlow<RegisterCodeState>(RegisterCodeState.None)
    val codeState = _codeState.asStateFlow()

    private lateinit var userInfo: UserInfoResponse
    private lateinit var accessToken: String
    private lateinit var integUid: String

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchUserInfo()
        }
    }

    fun setMyCode(code: String?) {
        myRecommendCode = code
    }

    fun setClipboard() {
        val clip: ClipData =
            ClipData.newPlainText(
                "my_recommend_code",
                myRecommendCode
            )
        clipboardManager.setPrimaryClip(clip)
    }

    private fun fetchUserInfo() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(integUid))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                userInfo = response.data
                _codeState.update {
                    RegisterCodeState.Success(RecommendCodeState.USERINFO)
                }
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _codeState.update {
                    RegisterCodeState.Error(Throwable(response.code))
                }
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private suspend fun registerFriendRecommendCode(code: String) {
        Timber.d("register recommend code : $code")

        _codeState.update {
            RegisterCodeState.Loading
        }
        val referral = hashMapOf(
            "integUid" to integUid,
            "referralCode" to code
        )
        when (val response = userInfoRepository.registerReferralCode(accessToken, referral)) {
            is ResultWrapper.Success -> {
                Timber.d("response.data : ${response.data}")
                _codeState.update {
                    RegisterCodeState.Success(RecommendCodeState.VALID)
                }
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _codeState.update {
                    RegisterCodeState.Error(Throwable(response.code))
                }
            }
            is ResultWrapper.NetworkError -> {
                Timber.d("network error")
                _codeState.update {
                    RegisterCodeState.Error(Throwable("Network Error"))
                }
            }
        }
    }

    fun checkInvalidCode(code: String) = viewModelScope.launch {
        when {
            code.isEmpty() -> {
                _codeState.update {
                    RegisterCodeState.Success(RecommendCodeState.EMPTY)
                }
            }
            code == myRecommendCode -> {
                _codeState.update {
                    RegisterCodeState.Success(RecommendCodeState.MYSELF)
                }
            }
            else -> {
                registerFriendRecommendCode(code)
            }
        }
    }

    fun checkVisibleCodeInput(): Boolean {
        return checkTwelveHours() or userInfo.useReferralCode.isNullOrEmpty()
    }

    private fun checkTwelveHours(): Boolean {
        val validDateTime = TimeUtils.getParsedLocalDateTime(userInfo.cdate)
            .plusHours(CODE_INPUT_VALID_HOUR.toLong())
        val now = TimeUtils.getCurrentLocalDateTime()

        Timber.d("validDateTime : $validDateTime")
        Timber.d("          now : $now")

        return validDateTime.isAfter(now)
    }

    companion object {
        const val CODE_INPUT_VALID_HOUR = 12
    }
}

enum class RecommendCodeState {
    EMPTY, INVALID, VALID, MYSELF, USERINFO
}

sealed class RegisterCodeState {
    object None : RegisterCodeState()
    object Loading : RegisterCodeState()
    data class Success(val codeState: RecommendCodeState) : RegisterCodeState()
    data class Error(val exception: Throwable) : RegisterCodeState()
}
