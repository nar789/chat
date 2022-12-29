package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatSearchResult
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatUserInfo
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddChatViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatRepository: ChatRepository): ViewModel() {

    private val followListResult = makeTmpFollowerList()
    private val _followList = followListResult.toMutableStateList()
    val followList: List<ChatUserInfo> get() = _followList

    private val _fantooList = mutableStateListOf<ChatUserInfo>()
    val fantooList: List<ChatUserInfo> get() = _fantooList

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> get() = _searchQuery

    val showEmptyFollow get() = followList.isEmpty()

    val tmpSearchResult = ChatSearchResult().apply {
        this.followList = makeTmpFollowerList()
        this.fantooList = makeTmpFantooList()
    }

    private val _checkedUserList = mutableStateListOf<ChatUserInfo>()
    val checkedUserList: List<ChatUserInfo> get() = _checkedUserList

    private val onCreateChat = chatRepository.createConversationResult
    val navigateToChat: LiveData<Int> = onCreateChat.map { if (it.first.not()) -1 else it.second}
    val showErrorToast: LiveData<Boolean> = onCreateChat.map { it.first.not() }

    private var myInfo: ChatUserInfo? = null

    // todo 전부 임시 코드
    private fun makeTmpFollowerList() = mutableListOf<ChatUserInfo>().apply {
        add(ChatUserInfo(name = "뚱땡이", profile = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAxODA1MjhfMjk4%2FMDAxNTI3NDg3MTczOTg3.Cj22WVbVuBhP7batfgThcIGge4dvykUJ0jA-cLsM1Fog.QuzRlxmV1UgmaPF-c4okqYFg4x6Ms_yT3f0ho_0zwSkg.JPEG.ehfkdl8989%2FKakaoTalk_Moim_4UjmLsR1AohJhEmSqqNZkX7uHKS4YV.jpg&type=sc960_832", id = "ft_u_a910f6fc7bbd11eda5c1952c36749daf_2022_12_14_14_43_23_385"))
        add(ChatUserInfo(name = "아이유", profile = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832", id = "TE_S_T1f581853e2311eda70aed9abcc3661d_2022_09_27_14_18_33_599"))
        add(ChatUserInfo(name = "유인나", profile = "https://search.pstatic.net/common/?src=http%3A%2F%2Fimgnews.naver.net%2Fimage%2F112%2F2014%2F08%2F14%2F201408141601113077159_20140814160212_01_99_20140814160305.jpg&type=sc960_832", id = "TE_S_T2f581853e2311eda70aed9abcc3661d_2022_09_27_14_18_33_599"))
    }

    private fun makeTmpFantooList() = mutableListOf<ChatUserInfo>().apply {
        add(ChatUserInfo(name = "박서준", profile = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA0MTVfMTI3%2FMDAxNjUwMDA2MTU3MjY3.TFZcQMzToy-rSgiPW2wa1b2DfZ-kG-5LALCm44JnalQg.bO24wtAh-IU9pm1RCxntbfNr20yYKeJitkIRAwII7ycg.JPEG.wizlecom%2F%25C1%25F6%25BF%25C0%25C1%25F6%25BE%25C6_X_%25B9%25DA%25BC%25AD%25C1%25D8_-_2.jpg&type=sc960_832", id="TE_S_T3f581853e2311eda70aed9abcc3661d_2022_09_27_14_18_33_599"))
        add(ChatUserInfo(name = "송강", profile = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjExMjFfNTQg%2FMDAxNjY5MDAxODcyMzI3.Y8UhB0seZ6Zptln10d1-XiDmrOJTna0FFPpqe2ze2mUg.rexYULAaowgP2b8vKOX9UFXrYn52rI6oz1W3kyZOknEg.JPEG.diaperiwinkle%2F162d2b34-81b5-4d09-93ad-2df4a1a2d8cc.jpeg&type=sc960_832", id="TE_S_T4f581853e2311eda70aed9abcc3661d_2022_09_27_14_18_33_599"))
    }

    init {
        viewModelScope.launch {
            dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString().let {
                myInfo = ChatUserInfo(id = it, name = "tmp", "")
            }
        }
    }

    fun onQueryInput(query: String) {
        _searchQuery.value = query

//        // todo 임시 코드, api호출 들어가야 함
        if (query.isNotBlank()) {
            _fantooList.clear()
            _fantooList.addAll(tmpSearchResult.fantooList ?: emptyList())

            _followList.clear()
            _followList.addAll(tmpSearchResult.followList ?: emptyList())
        } else {
            _fantooList.clear()
            _followList.clear()
            _followList.addAll(followListResult)
        }
    }

    fun onCheckStateChanged(user: ChatUserInfo) {
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
        chatRepository.requestCreateChat(makeRequestList())
    }

    private fun makeRequestList() = mutableListOf<ChatUserInfo>().apply {
        add(myInfo?: return@apply)
        addAll(checkedUserList)
    }
}