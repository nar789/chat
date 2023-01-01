package com.rndeep.fns_fantoo.ui.chatting.chat

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChattingSettingViewModel @Inject constructor(
    private val application: Application,
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
        _isAlarmOn.value = !isMute()
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
        changeMuteList(chatId, !onOff)
    }

    fun leaveChatting() {
        viewModelScope.launch {
            chatRepository.requestExitChatRoom(myUid, myName, chatId)
            delay(300)
            _popBackStackEvent.call()
        }
    }

    private fun changeMuteList(chatId: Int, isMute: Boolean) {
        val pref: SharedPreferences =
            application.getSharedPreferences(PREF_ALARM, Context.MODE_PRIVATE) ?: return

        val editor = pref.edit()
        val gson = Gson()
        val chatList = getMuteList().toMutableList()
        if (isMute) {
            chatList.add(chatId)
        } else {
            chatList.remove(chatId)
        }
        val json = gson.toJson(chatList)

        editor.putString(KEY_ALARM_STATE, json)
        editor.apply()
    }

    private fun isMute(): Boolean {
        return getMuteList().contains(chatId)
    }

    private fun getMuteList(): List<Int> {
        val pref: SharedPreferences =
            application.getSharedPreferences(PREF_ALARM, Context.MODE_PRIVATE)
                ?: return emptyList()

        val json: String = pref.getString(KEY_ALARM_STATE, "") ?: ""
        return Gson().fromJson(json, object : TypeToken<List<Int>?>() {}.type)?: emptyList()
    }

    companion object {
        private const val PREF_ALARM = "prefAlarm"
        private const val KEY_ALARM_STATE = "chatAlarmState"
    }
}