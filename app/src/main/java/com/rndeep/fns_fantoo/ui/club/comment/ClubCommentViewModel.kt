package com.rndeep.fns_fantoo.ui.club.comment

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.rndeep.fns_fantoo.data.local.model.InputCommentItem
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.TranslateRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.community.comment.CommunityCommentRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ERROR_COMMENT_IMAGE_UPLOAD_FAIL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ClubCommentViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val communityCommentRepository: CommunityCommentRepository,
    private val translateRepository: TranslateRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {

    private var accessToken: String? = null
    private var uId: String? = null
    var isUser =false
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

    //에러 코드 라이브 데이터
    private val _errorCodeLiveData = MutableLiveData<String>()
    val errorCodeLiveData: LiveData<String> get() = _errorCodeLiveData

    private var replyNextId :String?= null
    fun initNextId() {
        replyNextId = null
    }

    private val _reportMessageItems =SingleLiveEvent<Triple<List<BottomSheetItem>,Boolean,Int>>()
    val reportMessageItems :LiveData<Triple<List<BottomSheetItem>,Boolean,Int>> get() = _reportMessageItems
    var reportIdList = listOf<Int>()
    fun getReportMessage(isParent:Boolean, replyId:Int){
        viewModelScope.launch {
            communityCommentRepository.getReportMessageList().run {
                _reportMessageItems.value=Triple(
                    this.map {
                        BottomSheetItem(null,it.message,null,null,null)
                    },
                    isParent,
                    replyId
                )
                reportIdList=this.map {
                    it.reportMessageId
                }
            }
        }
    }

    //댓글 전송 누르는 순간의 데이터를 저장
    private var inputItem: InputCommentItem? = null

    //댓글 이미지 데이터
    private val _imageUrlLiveData = SingleLiveEvent<Uri?>()
    val imageUrlLiveData: LiveData<Uri?> get() = _imageUrlLiveData
    fun addOnImageList(imageUrl: Uri?) {
        _imageUrlLiveData.value = imageUrl
    }
    //댓글 텍스트
    private val _commentText = MutableLiveData<String>()
    val commentText : LiveData<String> get() = _commentText
    fun setCommentText(text :String){
        _commentText.value=text
    }
    //댓글 이미지 파일
    private val _attachItem  = MutableLiveData<List<ClubPostAttachList>?>()
    val attachItem : LiveData<List<ClubPostAttachList>?> get() =  _attachItem
    fun setAttachItem(selectAttachItem :List<ClubPostAttachList>?){
        this._attachItem.value=selectAttachItem
    }
    //변경 모드 여부
    private val _isModifyMode = MutableLiveData<Boolean>(false)
    val isModifyMode : LiveData<Boolean> get() = _isModifyMode
    fun setModifyMode(isModify :Boolean){
        _isModifyMode.value=isModify
    }
    //수정 replyId
    var modifyReplyId :Int? = null

    private val _clubReplyLiveData = MutableLiveData<List<ClubReplyData>>()
    val clubReplyLiveData: LiveData<List<ClubReplyData>> get() = _clubReplyLiveData
    var commentState = 0
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
                    if(this.first!!.isNotEmpty()){
                        commentState=this.first!![0].status
                    }
                }
            }

        }
    }

    //클라우드 플레어 키값
    private val _cloudFlareKeyLiveData = MutableLiveData<String>()
    val cloudFlareKeyLiveData: LiveData<String> get() = _cloudFlareKeyLiveData
    fun sendImageToCloudFlare(cloudKey: String, file: MultipartBody.Part) {
        viewModelScope.launch {
            if (!isUser) {
                _errorCodeLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            _cloudFlareKeyLiveData.value =
                uploadRepository.fileUploadImageToCloudFlare(cloudKey, file)?:ERROR_COMMENT_IMAGE_UPLOAD_FAIL
        }
    }

    //대댓 작성
    private val _resultInputComment = SingleLiveEvent<Boolean>()
    val resultInputComment :LiveData<Boolean> get() = _resultInputComment
    fun inputClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int,
    ){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            if(inputItem ==null ) {
                _errorCodeLiveData.value = ConstVariable.ERROR_C1000
                _resultInputComment.value = false
                return@launch
            }
            val array :JsonArray = if (inputItem!!.attachUrl != null) {
                JsonArray().apply {
                    this.add(
                        JsonObject().apply {
                            this.addProperty("attach", inputItem!!.attachUrl)
                            this.addProperty("attachType", 0)
                        }
                    )
                }
            }else{
                JsonArray()
            }
            communityCommentRepository.registerClubCommentReply(
                clubId,categoryCode,postId,replyId,
                HashMap<String,Any>().apply {
                    this["integUid"] = uId!!
                    this["content"] = inputItem!!.commentString
                    this["langCode"] = dataStoreRepository.getString(ConstVariable.KEY_HEADER_LANG)?:"en"
                    if (inputItem!!.attachUrl != null) this["attachList"] = array
                }
            ).run {
                _resultInputComment.value=this==ConstVariable.RESULT_SUCCESS_CODE
                if(this!=ConstVariable.RESULT_SUCCESS_CODE){
                    _errorCodeLiveData.value=this?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }

            }
        }
    }
    //대댓글 수정 [클럽]
    fun modifyClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int,
        reReplyId: Int,
    ) {
        viewModelScope.launch {
            if (isUser) {
                if(inputItem ==null ) {
                    _errorCodeLiveData.value = ConstVariable.ERROR_C1000
                    _resultInputComment.value = false
                    return@launch
                }
                var sendAttachList:JsonObject? =null
                if(inputItem!!.attachUrl != null){
                    sendAttachList = JsonObject().apply {
                        addProperty("attach", inputItem!!.attachUrl)
                        addProperty("attachType", 0)
                    }
                }
                communityCommentRepository.requestModifyClubCommentReply(
                    clubId,
                    categoryCode,
                    postId,
                    replyId,
                    reReplyId,
                    HashMap<String, Any>().apply {
                        this["content"] = inputItem!!.commentString
                        this[ConstVariable.KEY_UID] = uId!!
                        if (sendAttachList != null) this["attachList"] = listOf(sendAttachList)
                        else this["attachList"] = listOf<JsonObject>()
                    }
                ).run {
                    _resultInputComment.value=this==ConstVariable.RESULT_SUCCESS_CODE
                    if(this!=ConstVariable.RESULT_SUCCESS_CODE){
                        _errorCodeLiveData.value=this?:ConstVariable.ERROR_WAIT_FOR_SECOND
                    }
                }
            } else {
                _errorCodeLiveData.value = ConstVariable.ERROR_NOT_MEMBER
            }
        }
    }

    //댓글 삭제
    private val _deleteResultLiveData = SingleLiveEvent<String>()
    val deleteResultLiveData: LiveData<String> get() = _deleteResultLiveData
    fun clickCommentDelete(clubId: String,categoryCode:String, postId:Int,replyId: Int,parentsReplyId: Int) {
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            if(replyId==parentsReplyId){
                communityCommentRepository.deleteClubComment(
                    clubId,
                    categoryCode,
                    postId,
                    replyId,
                    uId!!,
                    "이유[클럽장만 작성]"
                )
            }else{
                communityCommentRepository.deleteClubCommentReply(
                    clubId,
                    categoryCode,
                    postId,
                    replyId,
                    parentsReplyId,
                    uId!!,
                    "이유[클럽장만 작성]"
                )
            }.run {
                if (this == ConstVariable.RESULT_SUCCESS_CODE) {
                    _deleteResultLiveData.value = ConstVariable.RESULT_SUCCESS_CODE
                } else {
                    _errorCodeLiveData.value = this
                }
            }
        }
    }
    //계정 차단
    private val _blockAccountResultLiveData = SingleLiveEvent<Boolean>()
    val blockAccountResultLiveData: LiveData<Boolean> get() = _blockAccountResultLiveData
    fun clickBlockAccount(clubId :String,memberId :Int) {
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            communityCommentRepository.patchBlockClubMember(clubId,memberId,uId!!).run {
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _blockAccountResultLiveData.value=true
                }else{
                    _errorCodeLiveData.value=this
                }
            }
        }
    }

    //댓글 차단
    private val _blockCommentResultLiveData = SingleLiveEvent<Boolean>()
    val blockCommentResultLiveData: LiveData<Boolean> get() = _blockCommentResultLiveData
    fun clickBlockComment(clubId: String,categoryCode: String,  postId: Int,parentsReplyId:Int,replyId: Int) {
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            if(parentsReplyId==replyId){
                communityCommentRepository.patchClubCommentBlock(uId!!,clubId, categoryCode, postId, replyId).run {
                    _blockCommentResultLiveData.value=this==ConstVariable.RESULT_SUCCESS_CODE
                    if(this!=ConstVariable.RESULT_SUCCESS_CODE)_errorCodeLiveData.value=this
                }
            }else{
                communityCommentRepository.patchClubCommentReplyBlock(uId!!,clubId, categoryCode, postId,parentsReplyId, replyId).run {
                    _blockCommentResultLiveData.value=this==ConstVariable.RESULT_SUCCESS_CODE
                    if(this!=ConstVariable.RESULT_SUCCESS_CODE)_errorCodeLiveData.value=this
                }
            }
        }
    }

    //댓글 신고
    private val _reportResultLiveData = SingleLiveEvent<Boolean>()
    val reportResultLiveData: LiveData<Boolean> get() = _reportResultLiveData
    fun clickReportComment(reportPos:Int,clubId: String,categoryCode: String,postId: Int,replyId: Int){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            communityCommentRepository.patchReportClubComment(
                clubId,categoryCode,postId,replyId,uId!!,reportIdList[reportPos],null
            ).run {
                if(this==ConstVariable.RESULT_SUCCESS_CODE){
                    _reportResultLiveData.value=true
                }else{
                    _errorCodeLiveData.value=this
                }
            }
        }
    }

    //대댓글 신고
    fun clickReportCommentReply(reportPos:Int,clubId: String,categoryCode: String,postId: Int,replyId: Int,parentsReplyId: Int){
        viewModelScope.launch {
            if(!isUser){
                _errorCodeLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            communityCommentRepository.patchReportClubComment(
                clubId,categoryCode,postId,replyId,uId!!,reportIdList[reportPos],parentsReplyId
            ).run {
                if(this==ConstVariable.RESULT_SUCCESS_CODE){
                    _reportResultLiveData.value=true
                }else{
                    _errorCodeLiveData.value=this
                }
            }
        }
    }

    //댓글 번역
    private val _translateReply =SingleLiveEvent<Pair<Boolean,String>>()
    val translateReply : LiveData<Pair<Boolean,String>> get() = _translateReply
    private var originReply = HashMap<Int,String>()
    fun clickTranslateReply(comment :String,pos :Int,isTranslateYn :Boolean){
        viewModelScope.launch {
            if(isTranslateYn){
                _translateReply.value= Pair(false,originReply[pos]!!)
            }else{
                originReply[pos] = comment
                translateRepository.translateWords(
                    listOf(comment),
                    dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE) ?: "en"
                ).run {
                    this.first?.let {
                        if(it.isNotEmpty()){
                            _translateReply.value=Pair(true,it[0].text)
                        }
                    }
                    this.second?.let {
                        _errorCodeLiveData.value=it.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                    }
                }
            }
        }
    }

    //댓글 입력 상태
    private val _commnetInputing = SingleLiveEvent<Boolean>()
    val commentInputing: LiveData<Boolean> get() = _commnetInputing
    fun settingCommentInputingState(state: Boolean) {
        _commnetInputing.value = state
    }

    fun setCommentItem() {
        inputItem = InputCommentItem(
            commentText.value ?: "",
            if(attachItem.value.isNullOrEmpty()) null
            else attachItem.value!![0].attach,
            if(attachItem.value.isNullOrEmpty()) null
            else "image",
            false,
        )
    }

    fun setImageDataInCommentItem(ids: String, type: String) {
        inputItem?.let {
            it.attachType = type
            it.attachUrl = ids
        }
    }

}