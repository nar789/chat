package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.runtime.mutableStateListOf
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.model.chat.ReadInfo

data class ChatUiState(
    val messages: List<Message> = mutableStateListOf(),
    val myId: String = "",
    val translateMode: Boolean = false,
    val blocked: Boolean = false,
    val readInfos: List<ReadInfo> = listOf()
)