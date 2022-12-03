package com.rndeep.fns_fantoo.ui.community.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.ui.community.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunitySearchViewModel @Inject constructor(
    private val communityReposit: CommunityRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var accessToken :String? =null
    private var uId : String? =null
    private var isUser =false
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

    private var currentNextId = 0

    private val _loadingStateLiveData = SingleLiveEvent<Boolean>()
    val loadingStateLiveData: LiveData<Boolean> get() = _loadingStateLiveData
    fun changeLoadingState(loadingState: Boolean) {
        _loadingStateLiveData.value = loadingState
    }
    //No More Item
    private val _noMoreSearchResult = MutableLiveData<Boolean>()
    val noMoreSearchResult : LiveData<Boolean> get() = _noMoreSearchResult

    //검색 단어
    private var saveSearchWord = ""
    //검색
    val searchPostDatas = MutableLiveData<List<BoardPagePosts>>()
    fun getSearchCommunityPost(searchWord: String) {
        viewModelScope.launch {
            saveSearchWord=searchWord
            val searchResultItems = communityReposit.getSearchResult(
                if(isUser) accessToken else null,
                if(isUser) uId else null ,
                saveSearchWord,
                currentNextId
            )

            if(searchResultItems.third!=null){

            }else if(searchResultItems.first!=null){
                currentNextId=searchResultItems.second!!
                searchPostDatas.value = searchResultItems.first!!
            }
        }
    }

    fun addSearchCommunityPost(){
        viewModelScope.launch {
            if(currentNextId==-1){
                _noMoreSearchResult.value=true
                return@launch
            }
            val searchResultItems = communityReposit.getSearchResult(
                if(isUser) accessToken else null,
                if(isUser) uId else null ,
                saveSearchWord,
                currentNextId
            )

            if(searchResultItems.third!=null){

            }else if(searchResultItems.first!=null){
                currentNextId=searchResultItems.second!!
                searchPostDatas.value = searchResultItems.first!!
            }
        }
    }

    private val _searchWordListLiveData = MutableLiveData<ArrayList<String>>()
    val searchWordListLiveData: LiveData<ArrayList<String>> get() = _searchWordListLiveData
    fun getCommunitySearchWordList() {
        viewModelScope.launch {
            val wordList =
                dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_COMMUNITYSEARCHWORDLIST)
            if (wordList == null) {
                _searchWordListLiveData.value = ArrayList<String>()
            } else {
                _searchWordListLiveData.value = arrayListOf<String>().apply {
                    addAll(wordList)
                    reverse()
                }
            }
        }
    }

    fun addCommunitySearchWordList(searchWord: String) {
        viewModelScope.launch {
            val searchList =
                dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_COMMUNITYSEARCHWORDLIST)
            if (searchList != null) {
                if (searchList.contains(searchWord)) {
                    searchList.remove(searchWord)
                }
                searchList.add(searchWord)
                _searchWordListLiveData.value = arrayListOf<String>().apply {
                    addAll(searchList)
                    reverse()
                }
                dataStoreRepository.putStringArray(
                    DataStoreKey.PREF_KEY_COMMUNITYSEARCHWORDLIST,
                    searchList
                )
            } else {
                _searchWordListLiveData.value = arrayListOf(searchWord)
                dataStoreRepository.putStringArray(
                    DataStoreKey.PREF_KEY_COMMUNITYSEARCHWORDLIST,
                    arrayListOf(searchWord)
                )

            }

        }
    }

    fun deleteWordOfCommunityList(deleteWord: String) {
        viewModelScope.launch {
            val searchList =
                dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_COMMUNITYSEARCHWORDLIST)
                    ?: return@launch
            searchList.remove(deleteWord)
            dataStoreRepository.putStringArray(
                DataStoreKey.PREF_KEY_COMMUNITYSEARCHWORDLIST,
                searchList
            )
        }
    }

    fun deleteAllWordOfCommunityList() {
        viewModelScope.launch {
            val searchList =
                dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_COMMUNITYSEARCHWORDLIST)
                    ?: return@launch
            searchList.clear()
            dataStoreRepository.putStringArray(
                DataStoreKey.PREF_KEY_COMMUNITYSEARCHWORDLIST,
                searchList
            )
        }
    }

    fun setInitSearchState(){
        currentNextId=0
    }

    fun getCurrentNextId()=currentNextId

}