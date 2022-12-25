package com.rndeep.fns_fantoo.ui.chatting.chatlist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatRoomModel
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.ChatUserRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatRepository: ChatRepository,
    private val chatUserRepository: ChatUserRepository
) : ViewModel(), DefaultLifecycleObserver {
    val chatList get() = chatRepository.chatList

    private val _optionOpenedChatId = MutableStateFlow<Int?>(null)

    val optionOpenedChatId: StateFlow<Int?> get() = _optionOpenedChatId
    private val _isUser = mutableStateOf(false)

    val isUser: State<Boolean> = _isUser

    private var userId: String = ""
    private val _navigateToLogin = SingleLiveEvent<Unit>()

    val navigateToLogin: LiveData<Unit> = _navigateToLogin

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                _isUser.value = it
            }
            dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString().let {
                userId = it
            }
        }
    }

    private fun loadChatList() {
        chatRepository.requestChatList(userId)
    }

    fun exitChat(chat: ChatRoomModel) {
        closeOptions(chat.id)
        chatRepository.requestExitChatRoom(chat.userId?: return, chat.title?: return, chat.id)
        chatRepository.requestChatList(userId)
    }

    fun blockChat(chatId: Int) {
        viewModelScope.launch {
            closeOptions(chatId)
            chatUserRepository.setConversationBlocked(userId, chatId, true)
        }
    }

    fun openOptions(chatId: Int) {
        _optionOpenedChatId.value = chatId
    }

    fun closeOptions(cardId: Int) {
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

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        loadChatList()
    }
}