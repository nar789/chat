package com.rndeep.fns_fantoo.ui.club.detail.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ClubDetailSearchViewModel @Inject constructor(
    private val clubRepository: ClubPageRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val TAG = javaClass.simpleName

    //검색
    val searchPostDatas = MutableLiveData<List<ClubPostData>>()
    private var searchNextId :Int? =null
    fun getSearchClubPost(clubId :String,searchWord: String) {
        viewModelScope.launch {
            if(searchNextId==-1) return@launch

            val size = if((searchPostDatas.value?.size?:0) > 10) searchPostDatas.value!!.size else 10
            val res = clubRepository.getSearchResult(clubId,searchWord,searchNextId,size)
            searchNextId=res.second
            res.first.let { listItem ->
                searchPostDatas.value=listItem
            }
            if(res.third!=null){


            }
        }
    }

    private val _searchWordListLiveData = MutableLiveData<ArrayList<String>>()
    val searchWordListLiveData :LiveData<ArrayList<String>> get() = _searchWordListLiveData
    fun getClubDetailSearchWordList() {
        viewModelScope.launch {
            val originalItem =
                dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_CLUBDETAILSEARCHWORDLIST)
            if(originalItem==null) {
                _searchWordListLiveData.value=ArrayList()
                return@launch
            }else{
                _searchWordListLiveData.value= arrayListOf<String>().apply {
                    addAll(originalItem)
                    reverse()
                }

            }
        }
    }

    fun addClubDetailSearchWord(word :String){
        viewModelScope.launch {
            val originalItem =
                dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_CLUBDETAILSEARCHWORDLIST)
            if(originalItem==null){
                _searchWordListLiveData.value= arrayListOf(word)
                dataStoreRepository.putStringArray(DataStoreKey.PREF_KEY_CLUBDETAILSEARCHWORDLIST,arrayListOf(word))
            }else{
                if (originalItem.contains(word)){
                    originalItem.remove(word)
                }
                originalItem.add(word)
                _searchWordListLiveData.value= arrayListOf<String>().apply {
                    addAll(originalItem)
                    reverse()
                }
                dataStoreRepository.putStringArray(DataStoreKey.PREF_KEY_CLUBDETAILSEARCHWORDLIST,originalItem)
            }
        }
    }

    fun deleteWordOfClubDetailWordList(deleteWord :String){
        viewModelScope.launch {
            val searchList = dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_CLUBDETAILSEARCHWORDLIST) ?: return@launch
            searchList.remove(deleteWord)
            dataStoreRepository.putStringArray(DataStoreKey.PREF_KEY_CLUBDETAILSEARCHWORDLIST,searchList)
        }
    }

    fun deleteAllWordOfClubDetailWordList(){
        viewModelScope.launch {
            val searchList = dataStoreRepository.getStringArray(DataStoreKey.PREF_KEY_CLUBDETAILSEARCHWORDLIST) ?: return@launch
            searchList.clear()
            dataStoreRepository.putStringArray(DataStoreKey.PREF_KEY_CLUBDETAILSEARCHWORDLIST,searchList)
        }
    }

}