package com.rndeep.fns_fantoo.ui.chatting.profiledetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ChatUserInfoResponse
import com.rndeep.fns_fantoo.repositories.ChatUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val chatUserRepository: ChatUserRepository,
) : ViewModel() {

    private val _profileUiState = mutableStateOf(ProfileUiState())
    val profileUiState: State<ProfileUiState> get() = _profileUiState

    private var targetUserId: String = ""
    private var accessToken: String = ""
    private var myUid: String = ""

    fun initProfileDetail(accessToken: String, myUid: String, userId: String) {
        Timber.d("initProfileDetail $userId")
        this.accessToken = accessToken
        this.myUid = myUid
        this.targetUserId = userId

        viewModelScope.launch {
            fetchChatUserInfo(userId)?.let { chatUserInfo ->
                _profileUiState.value = _profileUiState.value.copy(
                    blocked = chatUserInfo.blockYn,
                    followed = chatUserInfo.followYn,
                    name = chatUserInfo.userNick,
                    photo = chatUserInfo.userPhoto
                )
            }
        }
    }

    fun setUserBlock(blocked: Boolean) {
        viewModelScope.launch {
            _profileUiState.value = _profileUiState.value.copy(blocked = blocked)
            chatUserRepository.setUserBlock(accessToken, myUid, targetUserId, blocked)
        }
    }

    fun followUser(follow: Boolean) {
        viewModelScope.launch {
            _profileUiState.value = _profileUiState.value.copy(followed = follow)
            chatUserRepository.setUserFollow(accessToken, myUid, targetUserId, follow)
        }
    }

    private suspend fun fetchChatUserInfo(userId: String): ChatUserInfoResponse? {
        val response = chatUserRepository.fetchChatUserInfo(accessToken, myUid, userId)
        Timber.d("fetchChatUserInfo : $response")
        return when (response) {
            is ResultWrapper.Success -> response.data
            else -> null
        }
    }
}