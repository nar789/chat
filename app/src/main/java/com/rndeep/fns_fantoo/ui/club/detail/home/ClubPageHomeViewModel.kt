package com.rndeep.fns_fantoo.ui.club.detail.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.club.ClubNoticeItem
import com.rndeep.fns_fantoo.ui.common.post.CommonPostViewModel
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageRepository
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubPageHomeViewModel @Inject constructor(
    private val repository: ClubPageRepository,
    private val dataStoreRepository: DataStoreRepository
) : CommonPostViewModel(repository,dataStoreRepository) {

    private var nextId : Int? = null
    private var noticeNextId : Int? =null
    private val _noticeClubHomeMutableData =MutableLiveData<List<ClubNoticeItem>>()
    val noticeClubHomeMutableData :LiveData<List<ClubNoticeItem>> get() = _noticeClubHomeMutableData
    private val _detailClubHomePostItems = MutableLiveData<List<BoardPagePosts>>()
    val detailClubHomePostItems :LiveData<List<BoardPagePosts>> get() = _detailClubHomePostItems
    private val _errorDataClub =SingleLiveEvent<String>()
    val errorDataClub :LiveData<String> get() = _errorDataClub

    fun getClubDetailHomePostItem(type:String, clubId :String, categoryCode :String){
        viewModelScope.launch {
            val noticeData =repository.getClubHomeNoticeData(clubId,uId,noticeNextId)
            if(noticeData.second!=null){

            }
            _noticeClubHomeMutableData.value=noticeData.first!!
            noticeNextId=noticeData.third
            val size :String = if((_detailClubHomePostItems.value?.size?:0) < 10 || type=="init") "10" else _detailClubHomePostItems.value!!.size.toString()
            repository.getDetailHomePostData(
                type,
                clubId,
                categoryCode,
                isUser(),
                uId,
                nextId?.toString(),
                size,
                "sort",
                "Init"
            ).run {
                if (this.second != null) {
                    _errorDataClub.value=this.second!!.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }else{
                    _detailClubHomePostItems.value = this.first!!
                }
                nextId = this.third
            }
        }
    }

    fun addClubHomeData(type:String, clubId :String, categoryCode :String){
        viewModelScope.launch {
            if(nextId==-1)return@launch
            repository.getDetailHomePostData(
                type,
                clubId,
                categoryCode,
                isUser(),
                uId,
                nextId?.toString(),
                "10",
                "sort",
                "Add"
            ).run {
                if (this.second != null) {

                }
                _detailClubHomePostItems.value = this.first!!
                nextId = this.third
            }
        }
    }

    fun initNextIds(){
        nextId=null
        noticeNextId=null
    }
}