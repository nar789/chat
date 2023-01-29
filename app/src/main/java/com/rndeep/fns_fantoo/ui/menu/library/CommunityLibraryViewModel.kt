package com.rndeep.fns_fantoo.ui.menu.library


import androidx.lifecycle.*
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityMyPost
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityMyReply
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.MyLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommunityLibraryViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val myLibraryRepository: MyLibraryRepository
) : ViewModel() {

    private val _communityMyPosts = MutableLiveData<List<CommunityMyPost>>()
    val communityMyPosts: LiveData<List<CommunityMyPost>> = _communityMyPosts

    private val _communityMyComments = MutableLiveData<List<CommunityMyReply>>()
    val communityMyComments: LiveData<List<CommunityMyReply>> = _communityMyComments

    private val _communityMyBookmarks = MutableLiveData<List<CommunityMyPost>>()
    val communityMyBookmarks: LiveData<List<CommunityMyPost>> = _communityMyBookmarks

    private lateinit var accessToken: String
    private lateinit var integUid: String

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchCommunityMyPosts()
            fetchCommunityMyComments()
            fetchCommunityMyBookmarks()
        }
    }

    private fun fetchCommunityMyPosts() = viewModelScope.launch {
        val response = myLibraryRepository.fetchCommunityMyPosts(accessToken, integUid, 0, 10)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _communityMyPosts.value = response.data.postList
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

    private fun fetchCommunityMyComments() = viewModelScope.launch {
        val response = myLibraryRepository.fetchCommunityMyComments(accessToken, integUid, 0, 10)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _communityMyComments.value = response.data.replyList
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

    private fun fetchCommunityMyBookmarks() = viewModelScope.launch {
        val response = myLibraryRepository.fetchCommunityMyBookmarks(accessToken, integUid, 0, 10)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _communityMyBookmarks.value = response.data.postList
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