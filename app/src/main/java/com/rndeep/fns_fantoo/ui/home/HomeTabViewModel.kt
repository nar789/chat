package com.rndeep.fns_fantoo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeTabViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    var uId :String? = null
    var accessToken :String? = null
    private var isUser = false
    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isUser=it
                if(it){
                    accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
                    uId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
                }
            }
        }
    }

    fun isUser() = isUser
}