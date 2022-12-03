package com.rndeep.fns_fantoo.ui.menu.fantooclub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.FantooClubFollowResponse
import com.rndeep.fns_fantoo.data.remote.dto.FantooClubIsFollowResponse
import com.rndeep.fns_fantoo.data.remote.dto.FantooClubLikeResponse
import com.rndeep.fns_fantoo.data.remote.model.ClubBasicInfo
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.FantooClubRepository
import com.rndeep.fns_fantoo.ui.menu.fantooclub.category.Category
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.Contents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FantooClubViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val fantooClubRepository: FantooClubRepository
) : ViewModel() {

    private val _clubInfo = MutableLiveData<ClubBasicInfo?>()
    val clubInfo: LiveData<ClubBasicInfo?> = _clubInfo

    private val _isFollowInfo = MutableLiveData<FantooClubIsFollowResponse?>()
    val isFollowInfo: LiveData<FantooClubIsFollowResponse?> = _isFollowInfo

    private val _followInfo = MutableLiveData<FantooClubFollowResponse?>()
    val followInfo: LiveData<FantooClubFollowResponse?> = _followInfo

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _selectedContents = MutableLiveData<List<Contents>>()
    val selectedContents: LiveData<List<Contents>> = _selectedContents

    private var clubId = ""
    var isLogin = false
    private lateinit var integUid: String

    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isLogin = it
            }
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
        }
    }

    fun setClubId(clubId: String) {
        this.clubId = clubId
    }

    fun getClubId() = clubId

    fun fetchFantooClubBasicInfo() = viewModelScope.launch {
        val response = fantooClubRepository.fetchFantooClubBasicInfo(clubId)
        Timber.d("fetchFantooClubBasicInfo responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _clubInfo.value = response.data
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

    fun fetchFantooClubFollow() = viewModelScope.launch {
        val response = fantooClubRepository.fetchFantooClubFollow(clubId, integUid)
        Timber.d("fetchFantooClubFollow responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _isFollowInfo.value = response.data
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

    fun setFantooClubFollow() = viewModelScope.launch {
        val response = fantooClubRepository.setFantooClubFollow(clubId, IntegUid(integUid))
        Timber.d("setFantooClubFollow responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _followInfo.value = response.data
                _followInfo.value?.let {
                    _isFollowInfo.value?.followYn = it.followYn
                }
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

    fun setClickFollowBtn() {
        _clubInfo.value?.let { info ->
            info.favoriteYN = !info.favoriteYN
            _clubInfo.value = info
        }
        Timber.d("setClickFollowBtn clubInfo.followed : ${_clubInfo.value?.favoriteYN}")
    }
}