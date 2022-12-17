package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChattingSettingViewModel @Inject constructor() : ViewModel() {

    private var chatId: Long = 0

    private val _isAlarmOn = mutableStateOf(true)
    val isAlarmOn = _isAlarmOn

    private val _popBackStackEvent = SingleLiveEvent<Unit>()
    val popBackStackEvent = _popBackStackEvent

    private val _errorToastEvent = SingleLiveEvent<String>()
    val errorToastEvent = _errorToastEvent

    fun init(chatId: Long) {
        this.chatId = chatId
    }

    fun setAlarm(onOff: Boolean) {
        _isAlarmOn.value = onOff
    }

    fun leaveChatting() {
        // leave chatting
        val isSuccess = false
        if (isSuccess) {
            _popBackStackEvent.call()
        } else {
            _errorToastEvent.value = "채팅방 나가기 오류"
        }
    }
}