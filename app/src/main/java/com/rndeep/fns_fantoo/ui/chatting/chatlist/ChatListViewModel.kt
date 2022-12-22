package com.rndeep.fns_fantoo.ui.chatting.chatlist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.*
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatListResult
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatRepository: ChatRepository
) : ViewModel(), DefaultLifecycleObserver {
    private val _chatList = makeTmpChatList().toMutableStateList()
    val chatList: List<ChatListResult> get() = _chatList

    private val _optionOpenedChatId = MutableStateFlow<Long?>(null)
    val optionOpenedChatId: StateFlow<Long?> get() = _optionOpenedChatId

    private val _isUser = mutableStateOf(false)
    val isUser: State<Boolean> = _isUser

    private val _navigateToLogin = SingleLiveEvent<Unit>()
    val navigateToLogin: LiveData<Unit> = _navigateToLogin

    val conversationList = chatRepository.chatList

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                _isUser.value = it
            }
        }
    }

    private fun makeTmpChatList(): List<ChatListResult> = mutableListOf<ChatListResult>().apply {
        (0L..30L).forEach {
            add(ChatListResult(chatId = it, count = it))
        }
    }

    fun exitChat(chatId: Long) {
        closeOptions(chatId)
        _chatList.removeIf { it.chatId == chatId }
    }

    fun openOptions(chatId: Long) {
        _optionOpenedChatId.value = chatId
    }

    fun closeOptions(cardId: Long) {
        if (_optionOpenedChatId.value != cardId) {
            return
        }
        _optionOpenedChatId.value = null
    }

    fun navigateToLogin() {
        _navigateToLogin.call()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        chatRepository.startSocket()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        chatRepository.finish()
        super.onDestroy(owner)
    }
}