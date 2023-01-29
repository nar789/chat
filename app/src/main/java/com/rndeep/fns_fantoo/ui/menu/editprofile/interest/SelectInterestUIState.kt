package com.rndeep.fns_fantoo.ui.menu.editprofile.interest


sealed class SelectInterestUIState {
    object None : SelectInterestUIState()
    object Loading : SelectInterestUIState()
    data class Success(val state: Boolean) : SelectInterestUIState()
    data class Error(val exception: Throwable) : SelectInterestUIState()
}
