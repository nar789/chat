package com.rndeep.fns_fantoo.ui.menu.editprofile

sealed class EditProfileUIState {
    object None : EditProfileUIState()
    object Loading : EditProfileUIState()
    data class Success(val codeState: EditProfileState) : EditProfileUIState()
    data class Error(val exception: Throwable) : EditProfileUIState()
}