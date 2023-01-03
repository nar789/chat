package com.rndeep.fns_fantoo.ui.chatting.chatlist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatRoomInfo
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.ChatInfoRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatRepository: ChatRepository,
    private val chatInfoRepository: ChatInfoRepository
) : ViewModel(), DefaultLifecycleObserver {
    val chatList get() = chatRepository.chatList

    private val _optionOpenedChatId = MutableStateFlow<Int?>(null)

    val optionOpenedChatId: StateFlow<Int?> get() = _optionOpenedChatId
    private val _isUser = mutableStateOf(false)

    private val _muteChatIds = mutableStateListOf<Int>()
    val muteChatIds: List<Int> = _muteChatIds

    private val _updateMuteState = SingleLiveEvent<Pair<Int, Boolean>>()
    val updateMuteChatState: LiveData<Pair<Int, Boolean>> = _updateMuteState

    val isUser: State<Boolean> = _isUser

    private var userId: String = ""
    private val _navigateToLogin = SingleLiveEvent<Unit>()


    val navigateToLogin: LiveData<Unit> = _navigateToLogin

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _isUser.value = dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?: false
            userId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?: ""
        }
    }

    private fun loadChatList() {
        chatRepository.requestChatList(userId)
    }

    fun exitChat(chat: ChatRoomInfo) {
        closeOptions(chat.id)
        // TODO : change name chat.title to userName
        chatRepository.requestExitChatRoom(chat.userId?: return, chat.title?: return, chat.id)
        chatRepository.requestChatList(userId)
    }

    fun blockChat(chatId: Int) {
        viewModelScope.launch {
            launch(Dispatchers.Main) {
                closeOptions(chatId)
            }
            chatInfoRepository.setConversationBlocked(userId, chatId, true)
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

    fun updateMuteChatIds(list: List<Int>) {
        _muteChatIds.clear()
        _muteChatIds.addAll(list)
    }

    fun updateChatMuteState(chatId: Int, isMute: Boolean) {
        closeOptions(chatId)
        _updateMuteState.value = chatId to isMute
    }
}