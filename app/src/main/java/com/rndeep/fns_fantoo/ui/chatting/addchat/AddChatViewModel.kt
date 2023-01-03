package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse
import com.rndeep.fns_fantoo.data.remote.model.chat.CreateChatUserInfo
import com.rndeep.fns_fantoo.repositories.ChatInfoRepository
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddChatViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatRepository: ChatRepository,
    private val chatInfoRepository: ChatInfoRepository
) : ViewModel() {

    private var myId: String = ""
    private var accessToken: String = ""
    private var myUserInfo: CreateChatUserInfo? = null

    val followList: Flow<PagingData<GetUserListResponse.ChatUserDto>> by lazy { requestFollowList() }

    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchList: Flow<PagingData<GetUserListResponse.ChatUserDto>> =
        _searchQuery.filter { it.length >= 2 }.debounce(500).flatMapLatest { requestSearchList(it) }
    val searchQuery: Flow<String> get() = _searchQuery

    private val _checkedUserList = mutableStateListOf<GetUserListResponse.ChatUserDto>()
    val checkedUserList: List<GetUserListResponse.ChatUserDto> get() = _checkedUserList

    private val _navigateToChat = SingleLiveEvent<Int>()
    val navigateToChat: LiveData<Int> = _navigateToChat

    private val _showErrorToast = SingleLiveEvent<Boolean>()
    val showErrorToast: LiveData<Boolean> = _showErrorToast

    init {
        initUser()
        collectChat()
    }

    private fun collectChat() {
        viewModelScope.launch {
            chatRepository.createConversationResult.filterNotNull().collect { chatId ->
                _navigateToChat.value = chatId
            }

            chatRepository.showErrorToast.filterNotNull().collect {
                showErrorToast(false)
            }
        }
    }

    private fun initUser() {
        viewModelScope.launch {
            try {
                accessToken =
                    dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN) ?: ""

                myId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID) ?: ""

                chatInfoRepository.getMyProfile().onEach {
                    myUserInfo = CreateChatUserInfo(
                        userNick = it.nickname ?: "",
                        userPhoto = it.imageUrl ?: "",
                        id = myId
                    )
                }.collect()
            } catch (e: Exception) {
                showErrorToast(true)
                Timber.e("initUser error: ${e.message}", e)
            }
        }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun onCheckStateChanged(user: GetUserListResponse.ChatUserDto) {
        val findUser = checkedUserList.find { it.integUid == user.integUid }
        if (findUser != null) {
            _checkedUserList.remove(findUser)
        } else {
            _checkedUserList.add(user)
        }
    }

    fun onClickChatStart() {
        if (checkedUserList.isEmpty()) {
            return
        }
        chatRepository.requestCreateChat(makeRequestChatUserList())
    }

    private fun makeRequestChatUserList() = mutableListOf<CreateChatUserInfo>().apply {
        if (myUserInfo == null) {
            showErrorToast(true)
            return@apply
        }
        add(myUserInfo!!)
        addAll(CreateChatUserInfo.createList(checkedUserList))
    }

    private fun requestFollowList(): Flow<PagingData<GetUserListResponse.ChatUserDto>> =
        chatInfoRepository.getMyFollowList(myId, accessToken).cachedIn(viewModelScope)


    private fun requestSearchList(query: String) =
        chatInfoRepository.getSearchList(accessToken = accessToken, query = query, integUid = myId)
            .cachedIn(viewModelScope)

    private fun showErrorToast(exit: Boolean) {
        _showErrorToast.value = exit
    }
}