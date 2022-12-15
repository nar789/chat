package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatSearchResult
import com.rndeep.fns_fantoo.data.remote.model.chat.TmpUserInfo
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent

class AddChatViewModel : ViewModel() {
    private val followListResult = makeTmpFollowerList(0..30)
    private val _followList = followListResult.toMutableStateList()
    val followList: List<TmpUserInfo> get() = _followList

    private val _fantooList = mutableStateListOf<TmpUserInfo>()
    val fantooList: List<TmpUserInfo> get() = _fantooList

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> get() = _searchQuery

    val showEmptyFollow get() = followList.isEmpty()

    val tmpSearchResult = ChatSearchResult().apply {
        this.followList = makeTmpFollowerList(100..120)
        this.fantooList = makeTmpFollowerList(200..220)
    }

    private val _checkedUserList = mutableStateListOf<String>()
    val checkedUserList: List<String> get() = _checkedUserList

    private val _navigateToChat = SingleLiveEvent<Long>()
    val navigateToChat: LiveData<Long> = _navigateToChat

    private fun makeTmpFollowerList(range: IntRange) = mutableListOf<TmpUserInfo>().apply {
        range.withIndex().forEach { (index, num) ->
            add(
                TmpUserInfo(
                    userName = if (index == 0) "fajsdfkjasldjfaksdjfaksdjlfkajsdklfalksdkadsfjaslkdjfaklsdjfalksdjflkasd" else num.toString(),
                    userPhoto = if (index % 5 == 0) "" else "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832",
                    loginId = num.toString()
                )
            )
        }
    }

    fun onQueryInput(query: String) {
        _searchQuery.value = query

        // todo 임시 코드, api호출 들어가야 함
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

    fun onCheckStateChanged(loginId: String) {
        if (_checkedUserList.contains(loginId)) {
            _checkedUserList.remove(loginId)
        } else {
            _checkedUserList.add(loginId)
        }
    }

    fun onClickChatStart() {
        if (checkedUserList.isEmpty()) {
            return
        }
        //todo 채팅방생성 api 호출 필요
        _navigateToChat.value = 123L
    }
}