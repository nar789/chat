package com.rndeep.fns_fantoo.ui.community.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.data.remote.model.community.MyCommentItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityMyPageViewModel @Inject constructor(
    private val repository : CommunityMyRepository,
    private val dataStoreRepository: DataStoreRepository
    ) :ViewModel() {

    private var accessToken :String? =null
    var uId :String? =null
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

    private val _errorCodeLiveData = MutableLiveData<String>()
    val errorCodeLiveData : LiveData<String> get() = _errorCodeLiveData

    private var myPostNextId =0
    private val _myCreatePostLiveData = MutableLiveData<List<BoardPagePosts>>()
    val myCreatePostLiveData : LiveData<List<BoardPagePosts>> get() = _myCreatePostLiveData
    fun getMyPostData(type :String,size: String){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            if(type=="init") myPostNextId=0
            if(myPostNextId ==-1) return@launch

            repository.getMyCreatePostItem(accessToken!!,uId!!,myPostNextId,size,type).run {
                if(this.third!=null){
                    //Error
                }
                myPostNextId=this.second
                _myCreatePostLiveData.value=this.first!!
            }
        }
    }

    private var bookmarkNextId=0
    private val _myBookmarkPostData = MutableLiveData<List<BoardPagePosts>>()
    val myBookmarkPostData :LiveData<List<BoardPagePosts>> get() = _myBookmarkPostData
    fun getBookmarkPostData(type:String,size : String){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            if(type=="init") bookmarkNextId=0
            if(bookmarkNextId ==-1) return@launch

            repository.getMyBookmarkPostList(accessToken!!,uId!!,bookmarkNextId,size,type).run {
                if(this.third !=null){
                    _errorCodeLiveData.value=this.third?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                bookmarkNextId=this.second
                _myBookmarkPostData.value=this.first!!
            }
        }
    }

    var replyNextId =0
    private val _myCreateCommentLiveData = MutableLiveData<List<MyCommentItem>>()
    val myCreateCommentLiveData : LiveData<List<MyCommentItem>> get() = _myCreateCommentLiveData
    private var replyItem :List<MyCommentItem> = listOf()
    fun getMyCommentData(type: String,size :String){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            if(type=="init") {
                replyItem= listOf()
                replyNextId=0
            }
            if(replyNextId ==-1) return@launch
            repository.getMyCreateCommentItem(accessToken!!,uId!!,replyNextId,size).run {
                if(this.third!=null){
                    _errorCodeLiveData.value=this.third?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                replyNextId=this.second
                replyItem= replyItem + this.first!!
                _myCreateCommentLiveData.value=replyItem
            }
        }
    }

    private val _alaramOnOffLiveData = SingleLiveEvent<Boolean>()
    val alarmOnOffLiveData : LiveData<Boolean> get() = _alaramOnOffLiveData
    fun getComAlarmState(){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            repository.getCommunityAlarmState(uId!!).run {
                this.first?.let {
                    _alaramOnOffLiveData.value=it
                }
                this.second?.let {
                    _errorCodeLiveData.value=it.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
            }
        }
    }

    fun settingAlarm(){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            repository.changeCommunityAlarmState(uId!!).run {
                this.first?.let {
                    _alaramOnOffLiveData.value=it
                }
                this.second?.let {
                    _errorCodeLiveData.value=it.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
            }

        }
    }

}