package com.rndeep.fns_fantoo.ui.community.comment

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.rndeep.fns_fantoo.data.local.model.InputCommentItem
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.TranslateRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.utils.ConstVariable.ERROR_COMMENT_IMAGE_UPLOAD_FAIL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommunityCommentViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val communityCommentRepository: CommunityCommentRepository,
    private val translateRepository: TranslateRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {

    private var accessToken: String? = null
    var uId: String? = null
    private var isUser :Boolean =false
    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isUser=it
                accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
                uId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
            }
        }
    }

    private var replyNextId :String?= null
    fun initNextId() {
        replyNextId = null
    }

    fun isLoginUser()=isUser

    private var inputItem : InputCommentItem? =null

    //댓글 이미지 데이터
    private val _imageUrlLiveData = SingleLiveEvent<Uri?>()
    val imageUrlLiveData: LiveData<Uri?> get() = _imageUrlLiveData
    fun addOnImageList(imageUrl: Uri?) {
        _imageUrlLiveData.value = imageUrl
    }
    //댓글 텍스트
    private val _commentText =MutableLiveData<String>()
    val commentText :LiveData<String> get() = _commentText
    fun setCommentText(text :String){
        _commentText.value=text
    }
    //댓글 이미지 파일
    private val _attachItem  =MutableLiveData<List<DetailAttachList>?>()
    val attachItem :LiveData<List<DetailAttachList>?> get() =  _attachItem
    fun setAttachItem(selectAttachItem :List<DetailAttachList>?){
        _attachItem.value=selectAttachItem
    }

    fun setInitAttachImage(){
        _imageUrlLiveData.value = null
        _attachItem.value = null
    }
    //변경 모드 여부
    private val _isModifyMode = MutableLiveData<Boolean>(false)
    val isModifyMode :LiveData<Boolean> get() = _isModifyMode
    fun setModifyMode(isModify :Boolean){
        _isModifyMode.value=isModify
    }
    //수정 replyId
    var modifyReplyId :Int? = null

    //익명 여부
    private val _anonymYN = MutableLiveData<Boolean>(false)
    val anonymYN :LiveData<Boolean> get() = _anonymYN
    fun setAnonymYN(isAnonym : Boolean){
        _anonymYN.value=isAnonym
    }

    //에러 코드 라이브 데이터
    private val _errorCodeLiveData = MutableLiveData<String>()
    val errorCodeLiveData: LiveData<String> get() = _errorCodeLiveData

    //댓글 대댓글 정보
    private val _replyLiveData = MutableLiveData<List<CommunityReplyData>>()
    val communityReplyLiveData: LiveData<List<CommunityReplyData>> get() = _replyLiveData
    var commentState =1
    fun getDetailComment(postId: Int, replyId: Int) {
        viewModelScope.launch {
            val size = if((_replyLiveData.value?.size ?: 0) < 10) 10 else _replyLiveData.value?.size?:0
            val replyRes = communityCommentRepository.callCommunityComment(
                accessToken,
                isUser,
                uId,
                postId,
                replyId,
                replyNextId,
                size
            )

            if (replyRes.third != null) {

            } else if (replyRes.first != null) {
                replyNextId = replyRes.second!!
                _replyLiveData.value = replyRes.first!!
                if(replyRes.first!!.isNotEmpty()){
                    commentState=replyRes.first!![0].activeStatus
                }
            }
        }
    }

    private val _clubReplyLiveData = MutableLiveData<List<ClubReplyData>>()
    val clubReplyLiveData: LiveData<List<ClubReplyData>> get() = _clubReplyLiveData
    fun getClubDetailComment(clubId :String,categoryCode :String, postId :Int,replyId:Int){
        viewModelScope.launch {
            val size = if((_clubReplyLiveData.value?.size ?: 0) < 10) 10 else _clubReplyLiveData.value?.size?:0
            communityCommentRepository.callClubComment(
                uId,
                accessToken,
                clubId,
                categoryCode,
                postId,
                replyNextId,
                size,
                replyId
            ).run {
                if(this.second !=null){

                }
                if(this.first !=null){
                    _clubReplyLiveData.value=this.first!!
                    replyNextId=this.third
                }
            }

        }
    }

    //클라우드 플레어 키값
    private val _cloudFlareKeyLiveData = MutableLiveData<String>()
    val cloudFlareKeyLiveData: LiveData<String> get() = _cloudFlareKeyLiveData
    fun sendImageToCloudFlare(cloudKey: String, file: MultipartBody.Part) {
        viewModelScope.launch {
            if (accessToken == null || uId == null) {
                _errorCodeLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            _cloudFlareKeyLiveData.value =
                uploadRepository.fileUploadImageToCloudFlare(cloudKey, file)?:ERROR_COMMENT_IMAGE_UPLOAD_FAIL
        }
    }

    //대댓글 입력
    private val _inputCommentReplyLiveData = MutableLiveData<String>()
    val inputCommentReplyLiveData: LiveData<String> get() = _inputCommentReplyLiveData
    fun inputCommentReply(
        postId: String,
        targetId: String,
    ) {
        if (accessToken == null || uId == null) {
            _errorCodeLiveData.value = ConstVariable.ERROR_NOT_MEMBER
            return
        }
        if(inputItem ==null){
            _errorCodeLiveData.value = ConstVariable.ERROR_C1000
            _inputCommentReplyLiveData.value = ConstVariable.ERROR_WAIT_FOR_SECOND
            return
        }
        viewModelScope.launch {
            val sendAttachList = JsonObject().apply {
                addProperty("attachType", inputItem?.attachType)
                addProperty("id", inputItem?.attachUrl)
            }
            communityCommentRepository.clickCommentReply(
                accessToken!!,
                postId,
                targetId,
                HashMap<String, Any>().apply {
                    this["integUid"] = uId!!
                    this["anonymYn"] = inputItem!!.anonymYN
                    this["content"] = inputItem!!.commentString
                    if (inputItem?.attachUrl != null) this["attachList"] = listOf(sendAttachList)
                }).run {
                if (this == ConstVariable.RESULT_SUCCESS_CODE) {
                    _inputCommentReplyLiveData.value = this
                } else {
                    _errorCodeLiveData.value = this?: ConstVariable.ERROR_WAIT_FOR_SECOND
                }
            }
        }
    }

    //댓글 수정
    fun clickCommentModify(
        postId: String,
        replyId: String,
    ) {
        viewModelScope.launch {
            if (dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)==true) {
                if(inputItem==null){
                    _errorCodeLiveData.value = ConstVariable.ERROR_C1000
                    _inputCommentReplyLiveData.value = ConstVariable.ERROR_WAIT_FOR_SECOND
                    return@launch
                }
                val sendAttachList = JsonObject().apply {
                    addProperty("attachType", inputItem?.attachType)
                    addProperty("id", inputItem?.attachUrl)
                }
                communityCommentRepository.modifyCommentReply(
                    accessToken!!,
                    postId,
                    replyId,
                    HashMap<String, Any>().apply {
                        this["anonymYn"] = inputItem!!.anonymYN
                        this["content"] = inputItem!!.commentString
                        this[ConstVariable.KEY_UID] = uId!!
                        if (inputItem!!.attachType != null) this["attachList"] = listOf(sendAttachList )
                    }
                ).run {
                    if (this == 200) {
                        _inputCommentReplyLiveData.value = this.toString()
                    } else {
                        _errorCodeLiveData.value = this.toString() ?: ConstVariable.ERROR_NOT_MEMBER
                    }
                }
            } else {
                _errorCodeLiveData.value = ConstVariable.ERROR_NOT_MEMBER
            }
        }
    }

    //댓글 삭제
    private val _deleteResultLiveData = MutableLiveData<String>()
    val deleteResultLiveData: LiveData<String> get() = _deleteResultLiveData
    fun clickCommentDelete(postId: String, replyId: String) {
        viewModelScope.launch {
            communityCommentRepository.deleteCommentReply(
                accessToken!!,
                postId,
                replyId,
                HashMap<String, String>().apply {
                    this[ConstVariable.KEY_UID] = uId!!
                }
            ).run {
                if (this == ConstVariable.RESULT_SUCCESS_CODE) {
                    _deleteResultLiveData.value = ConstVariable.RESULT_SUCCESS_CODE
                } else {
                    _errorCodeLiveData.value = this
                }
            }
        }
    }

    private val _likeDisLikeResultLiveData = MutableLiveData<Triple<String, Int, Int>>()
    val likeDislikeResultLiveData: LiveData<Triple<String, Int, Int>> get() = _likeDisLikeResultLiveData
    fun clickLikeDisLikeComment(isCancel:Boolean,likeType:String,targetId:String) {
        viewModelScope.launch {
            if (accessToken == null || uId == null) {
                _errorCodeLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            } else {
                communityCommentRepository.callLikeDislikeComment(
                    accessToken!!,
                    isCancel,
                    likeType,
                    targetId,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID]=uId!!
                    }).run {
                    if(this.second!=null){
                        _errorCodeLiveData.value=this.second!!.code ?: ConstVariable.ERROR_WAIT_FOR_SECOND
                    }else{
                        _likeDisLikeResultLiveData.value=Triple(likeType,this.first!!.like,this.first!!.dislike)
                    }

                }
            }
        }
    }

    //계정 차단
    private val _blockAccountResultLiveData = MutableLiveData<Boolean>()
    val blockAccountResultLiveData: LiveData<Boolean> get() = _blockAccountResultLiveData
    fun clickBlockAccount(targetUid :String,isBlock :Boolean) {
        viewModelScope.launch {
            if(accessToken == null || uId== null){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            communityCommentRepository.callBlockCommentAccount(accessToken!!,uId!!,targetUid,isBlock).run {
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _blockAccountResultLiveData.value=true
                }else{
                    _errorCodeLiveData.value=this
                }
            }
        }
    }

    //댓글 차단
    private val _blockCommentResultLiveData = MutableLiveData<Boolean>()
    val blockCommentResultLiveData: LiveData<Boolean> get() = _blockCommentResultLiveData
    fun clickBlockComment(targetCommentId:String, targetUid:String, isBlock:Boolean) {
        viewModelScope.launch {
            if(accessToken == null || uId== null){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            communityCommentRepository.callBlockCommentOrPost(accessToken!!,targetCommentId,uId!!,targetUid,isBlock).run {
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _blockCommentResultLiveData.value=true
                }else{
                    _errorCodeLiveData.value=this
                }
            }
        }
    }

    //댓글 번역
    private val _translateComment = MutableLiveData<Pair<Boolean,String>>()
    val translateComment : LiveData<Pair<Boolean,String>> get() = _translateComment
    val originCommentMap = HashMap<Int,String>()
    fun clickTranslateComment(comment:String,translateYn : Boolean,pos :Int){
        viewModelScope.launch {
            if(translateYn){
                if(originCommentMap[pos]!=null){
                    _translateComment.value=Pair(false,originCommentMap[pos]!!)
                }
            }else{
                originCommentMap[pos]=comment
                translateRepository.translateWords(
                    listOf(comment),dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)?:"en"
                ).run {
                    this.first?.let {
                        if(it.isNotEmpty()){
                            _translateComment.value= Pair(true,it[0].text)
                        }
                    }
                    this.second?.let {
                        _errorCodeLiveData.value=it.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                    }
                }
            }

        }
    }

    //신고 메세지 리스트
    private val _reportMessageList = SingleLiveEvent<Triple<List<BottomSheetItem>,String,Int>>()
    val reportMessageList : LiveData<Triple<List<BottomSheetItem>,String,Int>> get() = _reportMessageList
    var reportIdList = listOf<Int>()
    fun getReportMessageList(targetId:Int,targetUid: String) {
        viewModelScope.launch {
            communityCommentRepository.getReportMessageList().run {
                _reportMessageList.value=Triple(
                    this.map {
                        BottomSheetItem(null,it.message,null,null,null)
                    },
                    targetUid,
                    targetId
                )
                reportIdList=this.map {
                    it.reportMessageId
                }
            }
        }
    }

    //댓글 신고
    private val _reportCommentResultLiveData = SingleLiveEvent<Boolean>()
    val reportCommentResultLiveData: LiveData<Boolean> get() = _reportCommentResultLiveData
    fun callReportPostComment(targetId:Int,targetUId:String,pos:Int){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            communityCommentRepository.callReportCommunityComment(
                targetId,
                uId!!,
                targetUId,
                reportIdList[pos]
            ).run {
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _reportCommentResultLiveData.value=true
                }else{
                    _errorCodeLiveData.value=this
                }

            }
        }
    }

    //댓글 입력 상태
    private val _commnetInputing = SingleLiveEvent<Boolean>()
    val commentInputing : LiveData<Boolean> get() = _commnetInputing
    fun settingCommentInputingState(state :Boolean){
        _commnetInputing.value=state
    }

    fun setCommentItem(){
        inputItem= InputCommentItem(
            commentText.value?:"",
            if (attachItem.value?.isNotEmpty() == true) {
                attachItem.value?.get(0)?.archiveType
            } else null,
            if (attachItem.value?.isNotEmpty() == true) {
                attachItem.value?.get(0)?.id
            } else null,
            anonymYN.value ?: false,
        )
    }

    fun setImageDataInCommentItem(ids:String,type:String){
        inputItem?.let {
            it.attachType=type
            it.attachUrl=ids
        }
    }

}