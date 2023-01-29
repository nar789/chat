package com.rndeep.fns_fantoo.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginPermissionViewModel @Inject constructor(private val dataStoreRepository: DataStoreRepository) :
    ViewModel() {

    fun setPermissinChecked(isPermissionChecked: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.putBoolean(
                DataStoreKey.PREF_KEY_IS_PERMISSION_CHECKED,
                isPermissionChecked
            )
        }
    }

}