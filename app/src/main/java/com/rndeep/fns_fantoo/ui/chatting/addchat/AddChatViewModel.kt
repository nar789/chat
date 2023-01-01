package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatUserInfo
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
import javax.inject.Inject

@HiltViewModel
class AddChatViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatRepository: ChatRepository,
    private val chatInfoRepository: ChatInfoRepository
) : ViewModel() {

    private var myId: String = ""
    private var accessToken: String = ""

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

    private val _showErrorToast = SingleLiveEvent<Unit>()
    val showErrorToast: LiveData<Unit> = _showErrorToast

    init {
        initUser()
        addChatCallback()
    }

    private fun addChatCallback() {
        viewModelScope.launch {
            chatRepository.createConversationResult.filterNotNull().collect { chatId ->
                _navigateToChat.value = chatId
            }

            chatRepository.showErrorToast.filterNotNull().collect {
                _showErrorToast.call()
            }
        }
    }

    private fun initUser() {
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
        chatInfoRepository.getMyFollowList(myId, accessToken).cachedIn(viewModelScope)


    private fun requestSearchList(query: String) =
        chatInfoRepository.getSearchList(accessToken = accessToken, query = query, integUid = myId)
            .cachedIn(viewModelScope)

    //todo 서버 연결 완성되면 지울 것
    fun makeTmpChatRoom() {
        chatRepository.requestTmpCreateChat(
            listOf(
                // 수지니
                ChatUserInfo(
                    id = "ft_u_a910f6fc7bbd11eda5c1952c36749daf_2022_12_14_14_43_23_385",
                    name = "오동통이",
                    profile = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAxOTA1MzFfMjAw%2FMDAxNTU5MjM0NTExNTgx.G_jRxyJkH-oguxh9WioKPq_UxxPoOuTB9UvFkE-AXHYg.214nLcB6kz0I_pyP6TO14EKjVVBGsb0dIS3SFMZhK0Ig.JPEG.mbc3088%2F5765FFB3-6C4D-43CE-8CB7-22C639380CD4.jpeg&type=sc960_832"
                ),
                //이나
                ChatUserInfo(
                    id = "ft_u_3f3042ff7b9e11edba62053f4f79e4b9_2022_12_14_10_58_31_353",
                    name = "이나",
                    profile = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjAxMjVfMjMy%2FMDAxNjQzMTAyOTg1NzI0.bkW6TJVG82Gi8uG643n5SaSTYOyEcNAq0Y7xsEkOBSUg.rU7SY3uYHJGnigm3WzvBk0LkXt_cO6UOyVsfeKxbEPAg.JPEG.minziminzi128%2FIMG_7370.JPG&type=sc960_832"
                )
            )
        )
    }
}