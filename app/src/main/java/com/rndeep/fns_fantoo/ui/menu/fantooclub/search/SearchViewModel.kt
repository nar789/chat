package com.rndeep.fns_fantoo.ui.menu.fantooclub.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost
import com.rndeep.fns_fantoo.repositories.FantooClubRepository
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.Contents
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.ContentsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val fantooClubRepository: FantooClubRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var clubId = ""

    private val _recentSearch = MutableLiveData<List<String>>(emptyList())
    val recentSearch: LiveData<List<String>> = _recentSearch

    private val _searchResult = MutableLiveData<List<FantooClubPost>>()
    val searchResult: LiveData<List<FantooClubPost>> = _searchResult

    private var recentSearchList = mutableListOf<String>()
    private var searchResultList = mutableListOf<Contents>()

    init {
        viewModelScope.launch {
            val recentSearch = dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_RECENT_NEWS_SEARCH)
            recentSearch?.let { searchList ->
                Timber.d("searchList: $searchList")
                recentSearchList = searchList
                _recentSearch.value = recentSearchList.reversed()
            }
        }
    }

    fun searchFantooClubPosts(keyword: String): Flow<PagingData<FantooClubPost>> {
        return fantooClubRepository.searchFantooClubPosts(clubId, keyword).cachedIn(viewModelScope)
    }

    fun setClubId(clubId: String) {
        this.clubId = clubId
    }

    fun getClubId() = clubId

    private fun saveSearchString() {
        viewModelScope.launch {
            dataStoreRepository.putStringArray(DataStoreKey.PREF_KEY_RECENT_NEWS_SEARCH, ArrayList(recentSearchList))
        }
    }

    fun addSearchString(str: String) {
        val item = recentSearchList.filter { it == str }
        if(item.isNotEmpty()) {
            Timber.d("remove item : ${item[0]}")
            recentSearchList.remove(item[0])
        }
        recentSearchList.add(str)
        saveSearchString()
        _recentSearch.value = recentSearchList.reversed()
    }

    fun removeSearchString(str: String) {
        recentSearchList.remove(str)
        saveSearchString()
        _recentSearch.value = recentSearchList.reversed()
    }

    fun removeAllSearchString() {
        recentSearchList.clear()
        saveSearchString()
        _recentSearch.value = recentSearchList
    }
}