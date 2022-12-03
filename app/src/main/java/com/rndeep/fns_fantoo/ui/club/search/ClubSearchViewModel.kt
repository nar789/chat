package com.rndeep.fns_fantoo.ui.club.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.model.club.ClubSearchItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubSearchViewModel @Inject constructor(
    private val repository: ClubSearchRepository,
    private val dataStoreRepository: DataStoreRepository
)  :ViewModel(){

    private val _hotClubItemLiveDate =MutableLiveData<List<ClubSearchItem>>()
    val hotClubItemLiveDate :LiveData<List<ClubSearchItem>> get() = _hotClubItemLiveDate
    fun getHotClubList(){
        viewModelScope.launch {
            _hotClubItemLiveDate.value=repository.getClubList(
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?:""
            )
        }
    }

    private val _clubSearchResultLiveData = MutableLiveData<List<ClubSearchItem>>()
    val clubSearchResultLiveData : LiveData<List<ClubSearchItem>> get() = _clubSearchResultLiveData
    private var searchNextId :Int? = null
    private var searchResultSize :Int = 0
    fun searchClub(searchWord :String){
        viewModelScope.launch {
            val size = if(searchResultSize<10)10 else searchResultSize
            repository.getSearchResultClub(searchWord,searchNextId,size).run {
                _clubSearchResultLiveData.value=this.first!!
                searchNextId=this.second
                searchResultSize=clubSearchResultLiveData.value?.size?:0
                if(this.third!=null){

                }
            }
        }
    }

    private val _searchWordListLiveData =MutableLiveData<ArrayList<String>>()
    val searchWordListLiveData :LiveData<ArrayList<String>> get() = _searchWordListLiveData
    fun getSearchWordList(){
        viewModelScope.launch {
            val wordList =dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_CLUBSEARCHWORDLIST)
            if(wordList ==null ){
                _searchWordListLiveData.value=ArrayList<String>()
            }else{
                _searchWordListLiveData.value= arrayListOf<String>().apply {
                    addAll(wordList)
                    reverse()
                }
            }
        }
    }


    fun addSearchWordList(searchWord: String){
        viewModelScope.launch {
            val searchList = dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_CLUBSEARCHWORDLIST)
            if(searchList!=null){
                if (searchList.contains(searchWord)){
                    searchList.remove(searchWord)
                }
                searchList.add(searchWord)
                _searchWordListLiveData.value= arrayListOf<String>().apply {
                    addAll(searchList)
                    reverse()
                }
                dataStoreRepository.putStringArray(DataStoreKey.PREF_KEY_CLUBSEARCHWORDLIST,searchList)
            }else{
                _searchWordListLiveData.value= arrayListOf(searchWord)
                dataStoreRepository.putStringArray(
                    DataStoreKey.PREF_KEY_CLUBSEARCHWORDLIST,
                    arrayListOf(searchWord))

            }

        }
    }

    fun deleteWordOfList(deleteWord :String){
        viewModelScope.launch {
            val searchList = dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_CLUBSEARCHWORDLIST) ?: return@launch
            searchList.remove(deleteWord)
            dataStoreRepository.putStringArray(DataStoreKey.PREF_KEY_CLUBSEARCHWORDLIST,searchList)
        }
    }

    fun deleteAllWordOfList(){
        viewModelScope.launch {
            val searchList = dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_CLUBSEARCHWORDLIST) ?: return@launch
            searchList.clear()
            dataStoreRepository.putStringArray(DataStoreKey.PREF_KEY_CLUBSEARCHWORDLIST,searchList)
        }
    }
}