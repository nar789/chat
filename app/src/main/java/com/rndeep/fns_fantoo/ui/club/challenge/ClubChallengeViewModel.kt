package com.rndeep.fns_fantoo.ui.club.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubChallengeViewModel @Inject constructor(
    private val clubChallengeRepository: ClubChallengeRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _loadingStateLiveData = SingleLiveEvent<Boolean>()
    val loadingStateLiveData: LiveData<Boolean> get() = _loadingStateLiveData
    fun changeLoadingState(loadingState: Boolean) {
        _loadingStateLiveData.value = loadingState
    }

    private val _challengeListLiveData = MutableLiveData<List<ClubChallengeItem>>()
    val challengeListLiveData: LiveData<List<ClubChallengeItem>> get() = _challengeListLiveData
    var challengeNextId :Int?=null
    var challengePostSize = 10

    fun getChallengeList() {
        viewModelScope.launch {
            val size =  if(challengePostSize<10) 10 else challengePostSize
            clubChallengeRepository.getChallengeList(
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?:"guest",
                size,
                challengeNextId
            ).run {
                _challengeListLiveData.value = this.first!!
                challengeNextId=this.second
                challengePostSize=_challengeListLiveData.value!!.size
            }
        }
    }
}