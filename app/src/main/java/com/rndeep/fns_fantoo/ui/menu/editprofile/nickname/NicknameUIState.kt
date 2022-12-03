package com.rndeep.fns_fantoo.ui.menu.editprofile.nickname


sealed class NicknameUIState {
    object None : NicknameUIState()
    object Loading : NicknameUIState()
    data class Success(val codeState: NicknameState) : NicknameUIState()
    data class Error(val exception: Throwable) : NicknameUIState()
}