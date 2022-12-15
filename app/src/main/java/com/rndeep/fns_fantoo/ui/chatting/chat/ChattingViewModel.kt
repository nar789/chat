package com.rndeep.fns_fantoo.ui.chatting.chat

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor() : ViewModel() {
    private val _chatUiState = mutableStateOf(testUiState)
    val chatUiState: State<ChatUiState> get() = _chatUiState

    fun sendMessage(message: String) {
        Log.d("sujini", "sendMesage: $message")
    }

    fun setTranslateMode(onOff: Boolean) {
        Log.d("sujini", "setTranslateMode: $onOff")
        _chatUiState.value = _chatUiState.value.copy(translateMode = onOff)
    }

    // temp code
    fun setUserBlock(blocked: Boolean) {
        _chatUiState.value = _chatUiState.value.copy(userBlocked = blocked)
    }
}

val testUiState = ChatUiState(
    messages = listOf(
        Message(
            content = "상암 경기장에서 공연한다는데 맞아? 장소 바뀐거 아니지?",
            authorName = "Dasol",
            authorImage = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832",
            timestamp = 1667283734000
        ),
        Message(
            content = "같이 갈꺼지? 공연 끝나고...",
            authorName = "Dasol",
            authorImage = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832",
            timestamp = 1667283734000
        ),
        Message(
            content = "당연히 같이 가야지~ 스탠딩 공연이잖아 너무 재밌을것 같어~",
            authorName = "Me",
            authorImage = null,
            timestamp = 1667290934000,
            unreadCount = 1
        ),
        Message(
            content = "하 빨리 다음주 됐으면...",
            authorName = "Me",
            authorImage = null,
            timestamp = 1667290934000,
            unreadCount = 1
        ),
        Message(
            authorName = "Me",
            authorImage = null,
            timestamp = 1667290994000,
            image = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832",
            unreadCount = 1
        ),
    )
)