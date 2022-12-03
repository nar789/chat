package com.rndeep.fns_fantoo.ui.menu.library

import androidx.lifecycle.*
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyDto
import com.rndeep.fns_fantoo.data.remote.dto.PostDto
import com.rndeep.fns_fantoo.repositories.MyLibraryRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ClubLibraryViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val myLibraryRepository: MyLibraryRepository
) : ViewModel() {

    private val _clubMyPosts = MutableLiveData<List<PostDto>>()
    val clubMyPosts: LiveData<List<PostDto>> = _clubMyPosts

    private val _clubMyComments = MutableLiveData<List<ClubStorageReplyDto>>()
    val clubMyComments: LiveData<List<ClubStorageReplyDto>> = _clubMyComments

    private val _clubMyBookmarks = MutableLiveData<List<PostDto>>()
    val clubMyBookmarks: LiveData<List<PostDto>> = _clubMyBookmarks

    private lateinit var accessToken: String
    private lateinit var integUid: String

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchClubMyPosts()
            fetchClubMyComments()
            fetchClubMyBookmarks()
        }
    }

    private fun fetchClubMyPosts() = viewModelScope.launch {
        val response = myLibraryRepository.fetchClubMyPosts(accessToken, integUid, "0", "10")
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _clubMyPosts.value = response.data.postList
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

    private fun fetchClubMyComments() = viewModelScope.launch {
        val response = myLibraryRepository.fetchClubMyComments(accessToken, integUid, "0", "10")
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _clubMyComments.value = response.data.replyList
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

    private fun fetchClubMyBookmarks() = viewModelScope.launch {
        val response = myLibraryRepository.fetchClubMyBookmarks(accessToken, integUid, "0", "10")
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _clubMyBookmarks.value = response.data.postList
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