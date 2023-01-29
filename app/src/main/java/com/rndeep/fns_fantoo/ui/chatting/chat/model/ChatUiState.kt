package com.rndeep.fns_fantoo.ui.chatting.chat.model

import androidx.paging.PagingData
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.model.chat.ReadInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class ChatUiState(
    val messages: Flow<PagingData<Message>> = flowOf(),
    val myId: String = "",
    val translateMode: Boolean = false,
    val blocked: Boolean = false,
    val readInfos: List<ReadInfo> = listOf(),
    val chatTitle: String? = null,
    val userCount: Int = 0
)