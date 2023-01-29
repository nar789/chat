package com.rndeep.fns_fantoo.ui.menu.mywallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.MyWalletResponse
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyWalletViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

//    private val _state = MutableStateFlow<NicknameUIState>(NicknameUIState.None)
//    val state = _state.asStateFlow()

    private val _myWallet = MutableLiveData<MyWalletResponse?>()
    val myWallet: LiveData<MyWalletResponse?> = _myWallet

    private lateinit var accessToken: String
    private lateinit var integUid: String

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchUserWallet()
        }
    }

    private fun fetchUserWallet() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserWallet(accessToken, integUid)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _myWallet.value = response.data
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

}