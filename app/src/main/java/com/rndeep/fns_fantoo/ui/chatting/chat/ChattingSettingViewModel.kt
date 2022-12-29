package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChattingSettingViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val userInfoRepository: UserInfoRepository,
) : ViewModel() {

    private var chatId: Int = 0
    private lateinit var accessToken: String
    private lateinit var userInfo: UserInfoResponse
    private lateinit var myUid: String
    private lateinit var myName: String

    private val _isAlarmOn = mutableStateOf(true)
    val isAlarmOn = _isAlarmOn

    private val _popBackStackEvent = SingleLiveEvent<Unit>()
    val popBackStackEvent = _popBackStackEvent

    init {
        viewModelScope.launch {
            dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?.let {
                myUid = it
            }
            accessToken =
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()

            fetchUserInfo()
        }
    }

    fun init(chatId: Int) {
        this.chatId = chatId
    }

    private fun fetchUserInfo() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(myUid))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                userInfo = response.data
                myName = userInfo.userNick.orEmpty()
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                Timber.d("network error")
            }
        }
    }

    fun setAlarm(onOff: Boolean) {
        _isAlarmOn.value = onOff
    }

    fun leaveChatting() {
        viewModelScope.launch {
            chatRepository.requestExitChatRoom(myUid, myName, chatId)
            _popBackStackEvent.call()
        }
    }
}