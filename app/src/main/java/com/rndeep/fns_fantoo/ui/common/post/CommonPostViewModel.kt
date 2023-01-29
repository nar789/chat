package com.rndeep.fns_fantoo.ui.common.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
open class CommonPostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    var accessToken :String? =null
    var uId :String? =null
    private var isUser : Boolean =false
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

    fun isUser()=isUser

    private val _errorMessageLiveData = SingleLiveEvent<Int>()
    val errorMessageLiveData: LiveData<Int> get() = _errorMessageLiveData
    private val _likeDislikeResultLiveData = SingleLiveEvent<Triple<String,List<BoardPagePosts>,Int>>()
    val likeDislikeResultLiveData: LiveData<Triple<String,List<BoardPagePosts>,Int>> get() = _likeDislikeResultLiveData
    fun requestLikeUpdate(postId: String, likeType:String,changePos :Int, postType:String, pkId: Int,isCancel:Boolean) {
        viewModelScope.launch {
            if(accessToken==null || uId==null){
                _errorMessageLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN.toInt()
                return@launch
            }
            repository.clickLikePost(pkId, postType, accessToken!!, likeType, postId, uId!!,isCancel).let {
                if(it.second!=null){
                    _errorMessageLiveData.value=it.second?.code?.toInt()?:ConstVariable.ERROR_WAIT_FOR_SECOND.toInt()
                }
                _likeDislikeResultLiveData.value= Triple(likeType,it.first,changePos)
            }
        }
    }
    private val _honorResultLiveData = SingleLiveEvent<Pair<List<BoardPagePosts>,Int>>()
    val honorResultLiveData: LiveData<Pair<List<BoardPagePosts>,Int>> get() = _honorResultLiveData
    fun requestHonorUpdate(postId: String,changePos :Int, postType:String, pkId: Int,isCancel:Boolean) {
     viewModelScope.launch {
            if(accessToken==null || uId==null){
                _errorMessageLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN.toInt()
                return@launch
            }
            repository.clickHonorPost(pkId,  accessToken!!, postType,postId, uId!!,isCancel).let {
                if(it.second!=null){
                    _errorMessageLiveData.value=it.second?.code?.toInt()?:ConstVariable.ERROR_WAIT_FOR_SECOND.toInt()
                }
                _honorResultLiveData.value= Pair(it.first,changePos)
            }
        }
    }


    //========common ============
    //화면 전환 및 회전시 observer 호출 되는 이슈로 인해 SingleLiveEvent 로 변경
    //저장하기
    val savePostResult = SingleLiveEvent<Boolean>()
    fun requestSavePost(pkId: Int, type: String) {
        viewModelScope.launch {
            if(accessToken==null || uId ==null){
                _errorMessageLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN.toInt()
            }
            repository.savePost(accessToken!!,uId!!,pkId).run {
                savePostResult.value = this
            }
        }
    }

    //공유하기
    val savePostSharePostResult = SingleLiveEvent<Boolean>()
    fun requestSharePost(id: Int, type: String) {
        savePostSharePostResult.value = true
    }

    //가입하기
    val savePostJoinClubResult = SingleLiveEvent<Boolean>()
    fun requestJoinClub(id: Int, type: String) {
        savePostJoinClubResult.value = true
    }

    //신고하기
    val savePostReportResult = SingleLiveEvent<Boolean>()
    fun requestReportPost(id: Int, type: String) {
        savePostReportResult.value = true
    }

    //게시글 숨기기
    private val _pieceBlockResultLiveData = SingleLiveEvent<Pair<List<BoardPagePosts>,Int>>()
    val pieceBlockResultLiveData :LiveData<Pair<List<BoardPagePosts>,Int>> get() = _pieceBlockResultLiveData
    fun requestBlockPiecePost(pkId: Int,changePos: Int) {
        viewModelScope.launch {
            if(accessToken ==null || uId==null){
                _errorMessageLiveData.value=ConstVariable.ERROR_POST_UPDATE_FAIL.toInt()
            }else{
                val result = repository.bolckPiecePost(pkId,accessToken!!,uId!!)
                if(result.second!=null){
                    _errorMessageLiveData.value=result.second?.code?.toInt()?:ConstVariable.ERROR_WAIT_FOR_SECOND.toInt()
                }
                _pieceBlockResultLiveData.value=Pair(result.first,changePos)
            }
        }
    }

    //이 사용자 차단하기
    private val _accountBlockResultLiveData = SingleLiveEvent<List<BoardPagePosts>>()
    val accountBlockResultLiveData :LiveData<List<BoardPagePosts>> get() = _accountBlockResultLiveData
    fun requestBlockPostUser(id: Int) {
        if(accessToken==null || uId==null){
            _errorMessageLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN.toInt()
            return
        }
        viewModelScope.launch {
            repository.blockUserPostFromDB(id,accessToken!!,uId!!).run {
                if(this.second!=null){
                    _errorMessageLiveData.value=this.second?.code?.toInt()?:ConstVariable.ERROR_WAIT_FOR_SECOND.toInt()
                }
                _accountBlockResultLiveData.value=this.first!!
            }
        }

    }

}