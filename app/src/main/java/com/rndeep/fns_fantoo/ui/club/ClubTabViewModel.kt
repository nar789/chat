package com.rndeep.fns_fantoo.ui.club

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendSelect
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem
import com.rndeep.fns_fantoo.data.remote.model.MainClub
import com.rndeep.fns_fantoo.data.remote.model.MyClubListItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.post.CommonPostViewModel
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ClubTabViewModel @Inject constructor(
    private val repository: ClubRepository,
    private val dataStoreRepository: DataStoreRepository
) : CommonPostViewModel(repository, dataStoreRepository) {

    private val _loadingStateLiveData = SingleLiveEvent<Boolean>()
    val loadingStateLiveData: LiveData<Boolean> get() = _loadingStateLiveData
    fun changeLoadingState(loadingState: Boolean) {
        _loadingStateLiveData.value = loadingState
    }

    //==================Club========================
    //전체 리스트
    val clubPostListLiveData = MutableLiveData<List<BoardPagePosts>>()

    //챌린지 리스트
    val challengeItemLiveData = MutableLiveData<List<ClubChallengeItem>>()
    //인기 추천 리스트
    val hotRecommendClubLiveData = MutableLiveData<CommonRecommendSelect>()

    //신규 추천 리스트
    val newRecommendClubLiveData = MutableLiveData<CommonRecommendSelect>()

    //내 클럽 리스트
    private val _myClubListLiveData = MutableLiveData<List<MyClubListItem>>()
    val myClubListLiveData :LiveData<List<MyClubListItem>> get() = _myClubListLiveData
    var myClubNextId :String? = null


    fun getInitClubData(context: Context) {
        viewModelScope.launch {
//            if(dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED) == true){
                //상단 myClub List
                val clubList = repository.getMyClubList(uId,myClubNextId)
                myClubNextId=clubList.second
                getClubList(clubList.first)
//            }
            //챌린지 리스트
//            getChallengeItem()
            //인기 클럽 추천
            getHotRecommendClub()
            //신규 클럽 추천
            getNewRecommendClub()
            val result = repository.getClubPostData(context)
            if (result.second != null) {

            }
            clubPostListLiveData.value = result.first!!
        }
    }

    //챌린지 리스트
    private suspend fun getChallengeItem() {
        repository.getChallengeItem(uId?:"guest",null).run {
            challengeItemLiveData.value = this
        }
    }

    //인기 추천 리스트
    private suspend fun getHotRecommendClub() {
        repository.getRecommendClub(
            ConstVariable.DB_CLUBPAGE_HOT,
            if(isUser()) uId!! else "null"
        ).run {
            if(this.second!=null){
//                hotRecommendClubLiveData.value= listOf()
            }
            this.first?.let {
                hotRecommendClubLiveData.value =it
            }
        }
    }

    //신규 추천 리스트
    private suspend fun getNewRecommendClub() {
        repository.getRecommendClub(
            ConstVariable.DB_CLUBPAGE_NEW,
            if(isUser()) uId!! else "null"
        ).run {
            if(this.second!=null){
//                newRecommendClubLiveData.value=
            }
            this.first?.let {
                newRecommendClubLiveData.value =it
            }
        }
    }


    //클럽 리스트
    private fun getClubList(data: List<MyClubListItem>) {
        _myClubListLiveData.value = data
    }
}