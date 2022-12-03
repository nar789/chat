package com.rndeep.fns_fantoo.ui.menu.fantooclub.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubCategory
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.FantooClubRepository
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.Contents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val fantooClubRepository: FantooClubRepository
) : ViewModel() {

    private var clubId = ""

    private val _categories = MutableLiveData<List<FantooClubCategory>>()
    val categories: LiveData<List<FantooClubCategory>> = _categories

    private val _selectedPosts = MutableLiveData<List<FantooClubPost>>()
    val selectedPosts: LiveData<List<FantooClubPost>> = _selectedPosts

    private var categoryList = mutableListOf<Category>()
    private var selectedContentsList = mutableListOf<Contents>()

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

    fun fetchFantooClubCategories(clubId: String) = viewModelScope.launch {
        val response = fantooClubRepository.fetchFantooClubCategories(clubId, getCategoryCode(), integUid)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _categories.value = response.data.categoryList
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

    fun fetchFantooClubPosts(clubId: String, categoryCode: String) = viewModelScope.launch {
        val response = fantooClubRepository.fetchFantooClubPosts(
            clubId,
            categoryCode,
            integUid,
            "n",
            "10"
        )
        Timber.d("fetchFantooClubHomePosts, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _selectedPosts.value = response.data.postList
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

    fun setClubId(clubId: String) {
        this.clubId = clubId
    }

    fun getClubId() = clubId

    private fun getCategoryCode() = when (clubId) {
        FANTOO_TV_ID -> CATEGORY_CODE_CHANNEL
        else -> CATEGORY_CODE_NEWS_SECTION
    }

    companion object {
        const val FANTOO_TV_ID = "fantoo_tv"
        const val CATEGORY_CODE_NEWS_SECTION = "section"
        const val CATEGORY_CODE_CHANNEL = "channel"
    }
}