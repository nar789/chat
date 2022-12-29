package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.ChatUserRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddChatViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatRepository: ChatRepository,
    private val chatUserRepository: ChatUserRepository
) : ViewModel() {

    private var myId: String = ""
    private var accessToken: String = ""

    val followList: Flow<PagingData<GetUserListResponse.ChatUserDto>> = requestFollowList()

    private val _searchQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchList: Flow<PagingData<GetUserListResponse.ChatUserDto>> =
        _searchQuery.flatMapLatest { requestSearchList(it) }
    val searchQuery: Flow<String> get() = _searchQuery

    private val _checkedUserList = mutableStateListOf<GetUserListResponse.ChatUserDto>()
    val checkedUserList: List<GetUserListResponse.ChatUserDto> get() = _checkedUserList

    private val onCreateChat = chatRepository.createConversationResult
    val navigateToChat: LiveData<Int> = onCreateChat.map { if (it.first.not()) -1 else it.second }
    val showErrorToast: LiveData<Boolean> = onCreateChat.map { it.first.not() }

    init {
        viewModelScope.launch {
            dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString().let {
                myId = it
            }
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN) ?: ""
        }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun onCheckStateChanged(user: GetUserListResponse.ChatUserDto) {
        if (_checkedUserList.contains(user)) {
            _checkedUserList.remove(user)
        } else {
            _checkedUserList.add(user)
        }
    }

    fun onClickChatStart() {
        if (checkedUserList.isEmpty()) {
            return
        }
        chatRepository.requestCreateChat(checkedUserList)
    }

    private fun requestFollowList(): Flow<PagingData<GetUserListResponse.ChatUserDto>> =
        chatUserRepository.getMyFollowList(myId, accessToken).cachedIn(viewModelScope)


    private fun requestSearchList(query: String) =
        chatUserRepository.getSearchList(accessToken = accessToken, query = query).cachedIn(viewModelScope)

}