package com.rndeep.fns_fantoo.ui.community.postdetail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.trans.TransMessage
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.TranslateRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.club.post.DetailPostRepository
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.community.postdetail.data.DetailPostItem
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CommunityDetailPostViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val detailPostRepository: DetailPostRepository,
    private val translateRepository: TranslateRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {

    private var accessToken: String? = null
    var uId: String? = null
    private var isUser = false

    fun isUser()=isUser

    //error code
    private val _detailPostErrorLiveData = SingleLiveEvent<String>()
    val detailPostErrorLiveData: LiveData<String> get() = _detailPostErrorLiveData

    //toolbar Item
    private val _postToolbarNameLiveData = MutableLiveData<String>()
    val postToolbarNameLiveData: LiveData<String> get() = _postToolbarNameLiveData

    //post item list
    private val _postDetailDataLiveData = MutableLiveData<List<DetailPostItem>>()
    val postDetailDataLiveData: LiveData<List<DetailPostItem>> get() = _postDetailDataLiveData

    //댓글 텍스트
    private val _commentContent =MutableLiveData<String?>()
    val commentContent:LiveData<String?> get() = _commentContent
    fun setCommentContent(text : String?){
        _commentContent.value=text
    }

    private val _replyId  =MutableLiveData<String>()
    val replyId :LiveData<String?> get() = _replyId
    fun setReplyId(selectReplyId: String){
        _replyId.value=selectReplyId
    }
    private val _anonymYN  =MutableLiveData<Boolean>()
    val anonymYN :LiveData<Boolean> get() = _anonymYN
    fun setAnonymYN(isAnonymYN :Boolean){
        this._anonymYN.value=isAnonymYN
    }
    private val _attachItem  =MutableLiveData<List<DetailAttachList>?>()
    val attachItem :LiveData<List<DetailAttachList>?> get() =  _attachItem
    fun setAttachItem(selectAttachItem :List<DetailAttachList>?){
        this._attachItem.value=selectAttachItem
    }

    private val _isModifyMode = MutableLiveData<Boolean>()
    val isModifyMode :LiveData<Boolean> get() = _isModifyMode
    fun setModifyMode(isModify :Boolean){
        _isModifyMode.value=isModify
    }

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

    //Community PostItem (게시판페이지 상세 일 시)
    private var postData :List<DetailPostItem>? = null
    fun getCommunityPostData(boardCode: String, postId: Int) {
        viewModelScope.launch {
            val detailResult =
                detailPostRepository.getCommunityPostDetail(accessToken, uId, boardCode, postId)
            if (detailResult.second != null) {

            } else if (detailResult.first != null) {
                val postItem =detailResult.first!!
                val communityPostData =detailResult.first!!.post
                replayNextId=0
                _postToolbarNameLiveData.value = communityPostData.subCode
                postData = listOf(
                    DetailPostItem.CommunityPostDetail(
                        ConstVariable.TYPE_COMMUNITY,
                        communityPostData,
                        postItem.attachList,
                        postItem.hashtagList,
                        postItem.openGraphList
                    )
                )
                getCommunityCommentList(postId.toString(), "init")
            }
        }
    }

    //NoticeItem (공지페이지 상세 일 시)
    fun requestCommunityNoticeDetailItem(boardCode: String?, postId: Int) {
        viewModelScope.launch {
            val safeRes = detailPostRepository.getCommunityNoticeDetailData(boardCode, postId)
            if (safeRes.second != null) {

            } else if (safeRes.first != null) {
                val noticeItem =safeRes.first!!
                _postToolbarNameLiveData.value = ""
                postData=listOf(
                    DetailPostItem.NoticePostDetail("Notice", noticeItem.notice,noticeItem.attachList)
                )
                _postDetailDataLiveData.value = postData!!

            }
        }
    }

    //커뮤니티 댓글 목록 받아오기
    private var replayNextId :Int? = 0
    fun getReplyNextId() = replayNextId
    private var commentList = arrayListOf<DetailPostItem.CommunityPostComment>()
    fun getCommunityCommentList(postId: String, type: String) {
        viewModelScope.launch {
            if(replayNextId==-1) return@launch
            val size = if(commentList.size<10 || type == "add") 10 else commentList.size
            val callRes = detailPostRepository.getPostCommentList(
                accessToken, uId, postId, replayNextId,size,isUser
            )

            if (callRes.third != null) {
                if(postData !=null){
                    _postDetailDataLiveData.value = postData!!
                }
            } else if (callRes.first != null) {
                if (postData != null) {
                    if (type == "init") {
                        commentList.clear()
                    }
                    callRes.first?.forEach {
                        commentList.add(DetailPostItem.CommunityPostComment("Comment", it))
                    }
                    _postDetailDataLiveData.value = postData!! + commentList
                }
                replayNextId = callRes.second!!
            }
        }
    }

    fun getCommunityCommentAllList(postId :String,type :String){
        viewModelScope.launch {
            if(type == "init"){
                replayNextId=0
                commentList.clear()
            }
            detailPostRepository.getPostCommentList(
                accessToken, uId, postId, replayNextId,30,isUser
            ).run {
                if(this.third !=null){

                }else if(this.first!=null){
                    this.first?.forEach {
                        commentList.add(DetailPostItem.CommunityPostComment("Comment", it))
                    }
                    replayNextId=this.second!!
                    if(replayNextId!=-1){
                        getCommunityCommentAllList(postId,"add")
                    }else{
                        _postDetailDataLiveData.value = postData!! + commentList
                    }
                }
            }
        }
    }

    //좋아요 선택 , 싫어요 선택 (clickType 으로 구분 )
    private val _likeClickResultLiveData = SingleLiveEvent<Triple<String, Int, Int>>()
    val likeClickResultLiveData: LiveData<Triple<String, Int, Int>> get() = _likeClickResultLiveData
    //좋아요 싫어요 취소 여부 결과
    private val _likeCancelResultLiveData =SingleLiveEvent<Triple<String, Int, Int>>()
    val likeCancelResultLiveData: LiveData<Triple<String, Int, Int>> get() = _likeCancelResultLiveData
    //isCancel : 취소 처리 여부 , clickType : like or Dislike , tartType : post or Reply
    fun clickPostLikeDislike(postId: String, isCancel :Boolean, clickType: String, targetType: String) {
        viewModelScope.launch {
            if (!isUser) {
                _detailPostErrorLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.callCommunityLickDisLikeClick(
                accessToken!!, isCancel, clickType, targetType, postId,
                HashMap<String, Any>().apply { this[ConstVariable.KEY_UID] = uId!! }
            ).run {
                if (this.second != null) {
                    _detailPostErrorLiveData.value = this.second!!.code?: ConstVariable.ERROR_WAIT_FOR_SECOND
                } else {
                    if(isCancel){
                        _likeCancelResultLiveData.value=
                            Triple(clickType, this.first!!.like, this.first!!.dislike)
                    }else{
                        _likeClickResultLiveData.value =
                            Triple(clickType, this.first!!.like, this.first!!.dislike)
                    }
                }
            }
        }
    }

    //아너 선택
    private val _honorClickResultLiveData = SingleLiveEvent<Pair<Boolean, Int>>()
    val honorClickResultLiveData: LiveData<Pair<Boolean, Int>> get() = _honorClickResultLiveData
    fun clickCommunityHonor() {
        viewModelScope.launch {
            detailPostRepository.callHonorClick().run {
                if (honorClickResultLiveData.value == null) {
                    _honorClickResultLiveData.value = Pair(true, 12)
                } else {
                    _honorClickResultLiveData.value = Pair(
                        !honorClickResultLiveData.value!!.first,
                        honorClickResultLiveData.value!!.second + 1
                    )

                }
            }
        }
    }

    //번역 선택[게시글]
    private val _translateClickResultLiveData = SingleLiveEvent<Pair<Boolean,DetailPostItem?>>()
    val translateClickResultLiveData: LiveData<Pair<Boolean,DetailPostItem?>> get() = _translateClickResultLiveData
    private var transOriginalItems = listOf<TransMessage>()
    fun clickTranslateContent(originTexts :List<String>,isTranslate:Boolean) {
        viewModelScope.launch {
            val item =postData?.get(0)
            if(isTranslate){
                if(!postData.isNullOrEmpty()){
                    when(item){
                        is DetailPostItem.CommunityPostDetail->{
                            transOriginalItems.forEach { transItem ->
                                if(item.item.content==transItem.text){
                                    item.item.content=transItem.origin
                                }
                                if(item.item.title==transItem.text){
                                    item.item.title=transItem.origin
                                }
                            }
                        }
                        is DetailPostItem.NoticePostDetail->{
                            transOriginalItems.forEach { transItem ->
                                if(item.item.content==transItem.text){
                                    item.item.content=transItem.origin
                                }
                                if(item.item.title==transItem.text){
                                    item.item.title=transItem.origin
                                }
                            }
                        }
                        else -> {}
                    }
                    _translateClickResultLiveData.value = Pair(false,item)
                }
            }else{
                translateRepository.translateWords(originTexts,dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)?:"en").run {
                    this.first?.let {
                        transOriginalItems=it
                        if(!postData.isNullOrEmpty()){
                            when(item){
                                is DetailPostItem.CommunityPostDetail->{
                                    it.forEach { transText->
                                        if(item.item.content==transText.origin){
                                            item.item.content=transText.text
                                        }
                                        if(item.item.title==transText.origin){
                                            item.item.title=transText.text
                                        }
                                    }
                                }
                                is DetailPostItem.NoticePostDetail->{
                                    it.forEach { transText->
                                        if(item.item.content==transText.origin){
                                            item.item.content=transText.text
                                        }
                                        if(item.item.title==transText.origin){
                                            item.item.title=transText.text
                                        }
                                    }
                                }
                                else -> {}
                            }
                            _translateClickResultLiveData.value = Pair(true,item)
                        }
                    }
                    this.second?.let {
                        _detailPostErrorLiveData.value=it.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                        _translateClickResultLiveData.value = Pair(false,null)
                    }
                }
            }
        }
    }

    //댓글 좋아요 선택
    private val _commentLikeClickResultLiveData = SingleLiveEvent<Triple<String,Int,Int>>()
    val commentLikeClickResultLiveData: LiveData<Triple<String,Int,Int>> get() = _commentLikeClickResultLiveData
    //댓글 좋아요 싫어요 취소 여부 결과
    private val _commentLikeCancelResultLiveData =SingleLiveEvent<Triple<String, Int, Int>>()
    val commentLikeCancelResultLiveData: LiveData<Triple<String, Int, Int>> get() = _commentLikeCancelResultLiveData
    fun clickCommentLike(likeType:String,targetId:String,isCancel: Boolean) {
        viewModelScope.launch {
            if(!isUser){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.callCommunityLickDisLikeClick(
                accessToken!!,
                isCancel,
                likeType,
                ConstVariable.LIKE_DISLIKE_TYPE_REPLY,
                targetId,
                hashMapOf<String, Any>().apply { this[ConstVariable.KEY_UID]=uId!! }
            ).run {
                if(this.second!=null){
                    _detailPostErrorLiveData.value=this.second!!.code?: ConstVariable.ERROR_WAIT_FOR_SECOND
                }else{
                    if(isCancel){
                        _commentLikeCancelResultLiveData.value = Triple(likeType, this.first!!.like,this.first!!.dislike)
                    }else{
                        _commentLikeClickResultLiveData.value = Triple(likeType, this.first!!.like,this.first!!.dislike)
                    }
                }
            }
        }
    }

    //대_댓글 좋아요, 싫어요 선택
    private val _replyLikeClickResultLiveData = SingleLiveEvent<Triple<String,Int,Int>>()
    val replyLikeClickResultLiveData: LiveData<Triple<String,Int,Int>> get() = _replyLikeClickResultLiveData
    //대_댓글 좋아요 싫어요 취소 여부 결과
    private val _commentReplyLikeCancelResultLiveData =SingleLiveEvent<Triple<String, Int, Int>>()
    val commentReplyLikeCancelResultLiveData: LiveData<Triple<String, Int, Int>> get() = _commentReplyLikeCancelResultLiveData
    fun clickCommentReplyLike(likeType:String,targetId:String,isCancel: Boolean) {
        viewModelScope.launch {
            if(!isUser){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.callCommunityLickDisLikeClick(
                accessToken!!,
                isCancel,
                likeType,
                ConstVariable.LIKE_DISLIKE_TYPE_REPLY,
                targetId,
                hashMapOf<String, Any>().apply { this[ConstVariable.KEY_UID]=uId!! }
            ).run {
                if(this.second!=null){
                    _detailPostErrorLiveData.value=this.second!!.code?: ConstVariable.ERROR_WAIT_FOR_SECOND
                }else{
                    if(isCancel){
                        _commentReplyLikeCancelResultLiveData.value = Triple(likeType, this.first!!.like,this.first!!.dislike)
                    }else{
                        _replyLikeClickResultLiveData.value = Triple(likeType, this.first!!.like,this.first!!.dislike)
                    }
                }
            }
        }
    }

    //게시글 북마크
    private val _bookmarkClickResultLiveData = SingleLiveEvent<Boolean>()
    val bookmarkClickResultLiveData: LiveData<Boolean> get() = _bookmarkClickResultLiveData
    fun clickBookmarkPost(postId: Int, bookmarkState: Boolean) {
        viewModelScope.launch {
            if (isUser) {
                if (bookmarkState) {
                    detailPostRepository.callUnBookmarkPostClick(accessToken!!, postId, uId!!)
                } else {
                    detailPostRepository.callBookmarkPostClick(accessToken!!, postId, uId!!)
                }.run {
                    when(this){
                        is ResultWrapper.Success->{
                            if (bookmarkClickResultLiveData.value == null) {
                                _bookmarkClickResultLiveData.value = true
                            } else {
                                _bookmarkClickResultLiveData.value =
                                    !bookmarkClickResultLiveData.value!!

                            }
                        }
                        is ResultWrapper.GenericError->{
                            _detailPostErrorLiveData.value = this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                        }
                        is ResultWrapper.NetworkError->{
                            _detailPostErrorLiveData.value = ConstVariable.ERROR_NETWORK
                        }
                    }
                }
            } else {
                _detailPostErrorLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }

        }
    }

    //댓글 신고
    private val _reportCommentResultLiveData = SingleLiveEvent<Boolean>()
    val reportCommentResultLiveData: LiveData<Boolean> get() = _reportCommentResultLiveData
    fun callReportPostComment(targetId:Int,type:String,targetUId:String,pos:Int){
        viewModelScope.launch {
            if(!isUser){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.reportCommunityPost(
                targetId,
                type,
                uId!!,
                targetUId,
                reportIdList[pos]
            ).run {
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _reportCommentResultLiveData.value=true
                }else{
                    _detailPostErrorLiveData.value=this
                }

            }
        }
    }

    //신고 메세지 리스트
    private val _reportMessageList = SingleLiveEvent<Triple<List<BottomSheetItem>,String,Int>>()
    val reportMessageList : LiveData<Triple<List<BottomSheetItem>,String,Int>> get() = _reportMessageList
    var reportIdList = listOf<Int>()
    fun getReportMessageList(type:String, targetId:Int,targetUid: String) {
        viewModelScope.launch {
            detailPostRepository.getReportMessageList().run {
                _reportMessageList.value=Triple(
                    this.map {
                        BottomSheetItem(null,it.message,null,null,null)
                    },
                    "${type}#&${targetUid}",
                    targetId
                )
                reportIdList=this.map {
                    it.reportMessageId
                }
            }
        }
    }

    //댓글 차단
    private val _blockCommentResultLiveData = SingleLiveEvent<Boolean>()
    val blockCommentResultLiveData: LiveData<Boolean> get() = _blockCommentResultLiveData
    fun clickBlockComment(targetCommentId:Int,targetUid: String,isBlock :Boolean) {
        viewModelScope.launch {
            if(!isUser){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.callBlockPostComment(accessToken!!,ConstVariable.LIKE_DISLIKE_TYPE_REPLY,targetCommentId,uId!!,targetUid,isBlock).run{
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _blockCommentResultLiveData.value=true
                }else{
                    _detailPostErrorLiveData.value=this
                }

            }
        }
    }

    //댓글 번역
    private val _transCommentResult = SingleLiveEvent<Pair<Boolean,String>>()
    val transCommentResult : LiveData<Pair<Boolean,String>> get() = _transCommentResult
    private var originComment : String =""
    private val originCommentMap : HashMap<Int,HashMap<Int,String>> =HashMap<Int,HashMap<Int,String>>()
    fun transCommentText(originText:String,isTranslate: Boolean,pos1 :Int,pos2:Int){
        viewModelScope.launch {
            if(isTranslate){
                if(!originCommentMap[pos1].isNullOrEmpty() && !(originCommentMap[pos1]!![pos2].isNullOrEmpty())){
                    _transCommentResult.value=Pair(false, originCommentMap[pos1]!![pos2]!!)
                }
            }else{
                if(originCommentMap[pos1]==null){
                    originCommentMap[pos1]=HashMap<Int, String>().apply {
                        this[pos2]=originText
                    }
                }else{
                    originCommentMap[pos1]!!.apply {
                        this[pos2]=originText
                    }
                }
                originComment=originText
                translateRepository.translateWords(
                    listOf(originText),
                    dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE) ?: "en"
                ).run {
                    this.first?.let {
                        if(it.isNotEmpty()){
                            _transCommentResult.value=Pair(true,it[0].text)
                        }
                    }
                    this.second?.let {
                        _detailPostErrorLiveData.value=it.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                    }
                }
            }
        }
    }

    //게시글 차단
    private val _blockPostResultLiveData = SingleLiveEvent<Boolean>()
    val blockPostResultLiveData: LiveData<Boolean> get() = _blockPostResultLiveData
    fun clickBlockPost(targetCommentId:Int,targetUid: String,isBlock :Boolean)  {
        viewModelScope.launch {
            if(!isUser){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.callBlockPostComment(accessToken!!,ConstVariable.LIKE_DISLIKE_TYPE_POST,targetCommentId,uId!!,targetUid,isBlock).run{
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _blockPostResultLiveData.value=true
                }else{
                    _detailPostErrorLiveData.value=this
                }
            }
        }
    }

    //게시글 삭제
    private val _deletePostResultLiveData =SingleLiveEvent<Boolean>()
    val deletePostResultLiveData :LiveData<Boolean> get() = _deletePostResultLiveData
    fun clickDeletePost(code :String, postId :Int,){
        viewModelScope.launch {
            if (!isUser) {
                _detailPostErrorLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.updateDeletePost(accessToken!!, uId!!, code, postId.toString()).run {
                if (this.second !=ConstVariable.RESULT_SUCCESS_CODE) {
                    _detailPostErrorLiveData.value = this.second!!
                } else {
                    _deletePostResultLiveData.value = this.first!!
                }
            }
        }
    }

    //댓글 사용자 차단
    private val _blockAccountResultLiveData = SingleLiveEvent<Boolean>()
    val blockAccountResultLiveData: LiveData<Boolean> get() = _blockAccountResultLiveData
    fun clickBlockAccountComment(targetUid:String,isBlock: Boolean) {
        viewModelScope.launch {
            if(!isUser){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.callBlockCommunityAccount(accessToken!!,uId!!, targetUid,isBlock).run {
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _blockAccountResultLiveData.value=true
                }else{
                    _detailPostErrorLiveData.value=this
                }
            }
        }
    }


    //댓글 수정
    private val _commentModifyResultLiveData = SingleLiveEvent<Boolean>()
    val commentModifyResultLiveData: LiveData<Boolean> get() = _commentModifyResultLiveData
    fun clickCommentModify(
        postId: String, comment: String, anonymYN: Boolean,
        attachUrl: String?, attachType: String?, replyId: String
    ) {
        viewModelScope.launch {
            if (isUser) {
                detailPostRepository.modifyComment(
                    accessToken!!,
                    uId!!,
                    postId,
                    comment,
                    anonymYN,
                    attachType,
                    attachUrl,
                    replyId
                ).run {
                    if (this == ConstVariable.RESULT_SUCCESS_CODE) {
                        _commentModifyResultLiveData.value = true
                    } else {
                        _detailPostErrorLiveData.value = this?:ConstVariable.ERROR_WAIT_FOR_SECOND
                        _commentModifyResultLiveData.value = false
                    }
                }
            } else {
                _detailPostErrorLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                _commentModifyResultLiveData.value = false
            }
        }
    }

    //댓글 삭제
    private val _commentDeleteResultLiveData = SingleLiveEvent<Boolean>()
    val commentDeleteResultLiveData: LiveData<Boolean> get() = _commentDeleteResultLiveData
    fun clickCommentDelete(postId: String, replyId: String) {
        viewModelScope.launch {
            if (isUser) {
                detailPostRepository.deleteComment(accessToken!!, uId!!, postId, replyId).run {
                    if (this == ConstVariable.RESULT_SUCCESS_CODE) {
                        _commentDeleteResultLiveData.value = true
                    } else {
                        _detailPostErrorLiveData.value = this
                    }
                }
            } else {
                _detailPostErrorLiveData.value = ConstVariable.ERROR_NOT_MEMBER
            }
        }
    }

    //댓글 입력
    private val _commentInputResultLiveData = SingleLiveEvent<String>()
    val commentInputResultLiveData: SingleLiveEvent<String> get() = _commentInputResultLiveData
    fun clickCommentSendBtn(
        commentContent: String,
        postId: Int,
        attachUrl: String?,
        attachType: String?
    ) {
        viewModelScope.launch {
            if (isUser) {
                detailPostRepository.callSendCommunityCommentData(
                    accessToken!!,
                    uId!!,
                    postId,
                    commentContent,
                    anonymYN.value?:false,
                    attachType,
                    attachUrl
                ).run {
                    if (this == ConstVariable.RESULT_SUCCESS_CODE) {
                        _commentInputResultLiveData.value = this
                    } else {
                        _detailPostErrorLiveData.value = this
                        _commentInputResultLiveData.value = this
                    }
                }
            } else {
                _commentInputResultLiveData.value = ConstVariable.ERROR_NOT_MEMBER
                _detailPostErrorLiveData.value = ConstVariable.ERROR_NOT_MEMBER
            }
        }
    }

    //댓글 이미지 서버 전송
    private val _cloudFlareUploadLiveData = SingleLiveEvent<String>()
    val cloudFlareUploadLiveData: LiveData<String> get() = _cloudFlareUploadLiveData
    fun sendImageToCloudFlare(cloudFlareKey: String, file: MultipartBody.Part) {
        viewModelScope.launch {
            uploadRepository.fileUploadImageToCloudFlare(cloudFlareKey, file).run {
                if (this == null) {
                    _detailPostErrorLiveData.value = ConstVariable.ERROR_WAIT_FOR_SECOND
                } else {
                    _cloudFlareUploadLiveData.value = this
                }
            }
        }
    }

    //댓글 이미지 입력
    private val _imageUrlLiveData = SingleLiveEvent<Uri?>()
    val imageUrlLiveData: LiveData<Uri?> get() = _imageUrlLiveData
    fun addOnImageList(imageUrl: Uri?) {
        _imageUrlLiveData.value = imageUrl
    }

    fun setInitAttachImaga(){
        _imageUrlLiveData.value = null
        _attachItem.value= null
    }


    fun setInitReplyNextId() {
        replayNextId = 0
    }
}