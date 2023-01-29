package com.rndeep.fns_fantoo.ui.menu.fantooclub.contents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.FantooClubLikeResponse
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.FantooClubRepository
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.MoreMenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContentsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val fantooClubRepository: FantooClubRepository
) : ViewModel() {

    private var clubId = ""

    private val _contents = MutableLiveData<List<Contents>>()
    val contents: LiveData<List<Contents>> = _contents

    private val _likeInfo = MutableLiveData<FantooClubLikeResponse?>()
    val likeInfo: LiveData<FantooClubLikeResponse?> = _likeInfo

    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> = _errorMsg


    val moreMenuItems = mutableListOf(
//        MoreMenuItem(R.drawable.icon_outline_save, listOf(R.string.j_save, R.string.j_save_cancel), false),
        MoreMenuItem(R.drawable.icon_outline_share, listOf(R.string.g_to_share), false),
//        MoreMenuItem(R.drawable.icon_outline_hide, listOf(R.string.a_block_post, R.string.a_see_post), false)
    )

    private val _homePosts = MutableLiveData<List<FantooClubPost>>()
    val homePosts: LiveData<List<FantooClubPost>> = _homePosts


    var isLogin = false
    lateinit var integUid: String

    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isLogin = it
            }
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
        }
    }

    fun fetchFantooClubHomePosts(clubId: String): Flow<PagingData<FantooClubPost>> {
        return fantooClubRepository.getFantooClubPostResultStream(
            clubId,
            CATEGORY_CODE_HOME,
            integUid
        ).cachedIn(viewModelScope)
    }

    fun setFantooClubPostLikeAndDislike(likeType: String, categoryCode: String, postId: String) =
        viewModelScope.launch {
            val response = fantooClubRepository.setFantooClubPostLikeAndDislike(
                clubId,
                likeType,
                categoryCode,
                postId,
                IntegUid(integUid)
            )
            Timber.d("setFantooClubLikeAndDislike, responseData : $response")
            when (response) {
                is ResultWrapper.Success -> {
                    _likeInfo.value = response.data
                }
                is ResultWrapper.GenericError -> {
                    Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                    if(response.code == "FE1013") {
                        _errorMsg.value = response.code
                    } else {
                        _errorMsg.value = response.message
                    }
                }
                is ResultWrapper.NetworkError -> {
                    // TODO handling network error
                    Timber.d("network error")
                }
            }
        }

    fun setClubId(clubId: String) {
        this.clubId = clubId
    }

    fun getClubId() = clubId

    fun setInitErrorMsg() {
        _errorMsg.value = null
    }

    companion object {
        const val CATEGORY_CODE_HOME = "home"
    }
}