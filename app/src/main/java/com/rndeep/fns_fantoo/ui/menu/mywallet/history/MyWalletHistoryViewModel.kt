package com.rndeep.fns_fantoo.ui.menu.mywallet.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.MyWalletHistoryResponse
import com.rndeep.fns_fantoo.data.remote.dto.MyWalletResponse
import com.rndeep.fns_fantoo.data.remote.model.userinfo.WalletHistoryType
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyWalletHistoryViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _myWallet = MutableLiveData<MyWalletResponse?>()
    val myWallet: LiveData<MyWalletResponse?> = _myWallet

    private val _myWalletHistory = MutableLiveData<MyWalletHistoryResponse?>()
    val myWalletHistory: LiveData<MyWalletHistoryResponse?> = _myWalletHistory

    private lateinit var accessToken: String
    private lateinit var integUid: String

    var currentHistoryType = WalletHistoryType.ALL

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

    fun fetchUserWalletHistory(
        walletType: String,
        walletListType: String,
        yearMonth: String,
        nextId: Int,
        size: Int
    ) = viewModelScope.launch {
        val response = userInfoRepository.fetchUserWalletHistory(accessToken, walletType, integUid, walletListType, yearMonth, nextId, size)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _myWalletHistory.value = response.data
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