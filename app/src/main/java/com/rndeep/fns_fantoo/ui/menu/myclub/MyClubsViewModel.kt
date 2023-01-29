package com.rndeep.fns_fantoo.ui.menu.myclub

import androidx.lifecycle.*
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.MyClubListItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.club.ClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyClubsViewModel @Inject constructor(
    private val clubRepository: ClubRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var isFavorite = false

    private val _clubCount = MutableLiveData<Int>()
    val clubCount: LiveData<Int> = _clubCount

    private val _myClubs = MutableLiveData<List<MyClubListItem>>()
    val myClubs: LiveData<List<MyClubListItem>> = _myClubs

    private lateinit var accessToken: String
    private lateinit var integUid: String

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_MY_CLUBS_IS_FAVORITE)?.let {
                isFavorite = it
            }
            fetchMyClubs(isFavorite)
        }
    }

    fun fetchMyClubs(isFavorite: Boolean) = viewModelScope.launch {
        val favorite = if (isFavorite) SORT_FAVORITE else null
        val response = clubRepository.fetchMyClubs(integUid, null, null, favorite)
        Timber.d("responseData : $response")
        when(response) {
            is ResultWrapper.Success -> {
                _clubCount.value = response.data.listSize
                _myClubs.value = response.data.clubList
            }
            else -> {

            }
        }
    }

    fun setFavorite(favorite: Boolean) = viewModelScope.launch {
        dataStoreRepository.putBoolean(DataStoreKey.PREF_KEY_MY_CLUBS_IS_FAVORITE, favorite)
    }

    fun getFavorite() = isFavorite

    companion object {
        private const val SORT_FAVORITE = "favorite"
    }
}