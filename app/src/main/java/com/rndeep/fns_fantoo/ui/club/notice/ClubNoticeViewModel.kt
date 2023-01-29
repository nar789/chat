package com.rndeep.fns_fantoo.ui.club.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubNoticeViewModel @Inject constructor(
    private val clubNoticeRepository: ClubNoticeRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    var uId :String?=null
    var isUser : Boolean =false
    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isUser=it
                uId=dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
            }
        }
    }

    private var nextId :Int? =null
    private val _clubNoticePostLiveData = MutableLiveData<List<ClubPostData>>()
    val clubNoticePostLiveData :LiveData<List<ClubPostData>> get() = _clubNoticePostLiveData
    fun getClubNoticeList(clubId:String){
        viewModelScope.launch {
            if(nextId==-1){
                return@launch
            }
            val size = if((_clubNoticePostLiveData.value?.size?:0)>10) _clubNoticePostLiveData.value?.size!! else 10

            clubNoticeRepository.fetchClubNoticeList(
                isUser,clubId,"notice",uId,nextId,size
            ).run {
                _clubNoticePostLiveData.value=(_clubNoticePostLiveData.value?:listOf()) + this.first
                nextId = this.second

            }
        }
    }
}