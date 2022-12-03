package com.rndeep.fns_fantoo.ui.club.post

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.data.remote.model.trans.TransMessage
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.TranslateRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.community.postdetail.data.DetailPostItem
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@HiltViewModel
class ClubPostDetailViewModel @Inject constructor(
    private val detailPostRepository: DetailPostRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val translateRepository: TranslateRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {

    private var accessToken: String? = null
    var uId: String? = null
    private var isLoginUser =false
    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isLoginUser=it
                if(it){
                    accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
                    uId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
                }
            }
        }
    }

    //error code
    private val _detailPostErrorLiveData = SingleLiveEvent<String>()
    val detailPostErrorLiveData: LiveData<String> get() = _detailPostErrorLiveData

    //toolbar Item
    private val _postToolbarNameLiveData = MutableLiveData<String>()
    val postToolbarNameLiveData: LiveData<String> get() = _postToolbarNameLiveData

    var isMember =false
    fun getIsLogin()=isLoginUser

    //post item list
    private var postData = listOf<DetailPostItem>()
    private var commentList = arrayListOf<DetailPostItem.ClubPostComment>()
    private val _postDetailDataLiveData = MutableLiveData<List<DetailPostItem>>()
    val postDetailDataLiveData: LiveData<List<DetailPostItem>> get() = _postDetailDataLiveData
    //게시글 북마크
    private val _bookmarkClickResultLiveData = MutableLiveData<Boolean>()
    val bookmarkClickResultLiveData: LiveData<Boolean> get() = _bookmarkClickResultLiveData

    //댓글
    private var isModifyMode : Boolean =false
    private var attachItem :List<ClubPostAttachList>? =null
    private var replayNextId :String? = null
    //댓글 텍스트
    private val _commentContent =MutableLiveData<String?>()
    val commentContent:LiveData<String?> get() = _commentContent
    //댓글 이미지 서버 전송
    private val _cloudFlareUploadLiveData = SingleLiveEvent<String>()
    val cloudFlareUploadLiveData: LiveData<String> get() = _cloudFlareUploadLiveData
    //댓글 이미지 입력
    private val _imageUrlLiveData = SingleLiveEvent<Uri?>()
    val imageUrlLiveData: LiveData<Uri?> get() = _imageUrlLiveData
    private var multipartBody :MultipartBody.Part? = null

    fun getMultiPartBody()=multipartBody

    fun setImageAttachList(attachList:List<ClubPostAttachList>){
        this.attachItem=attachList
    }
    fun setModifyMode(isModify: Boolean) {
        isModifyMode= isModify
    }
    fun getModifyMode()=isModifyMode

    //신고 메세지 리스트 가져오기
    private val _reportMessageList = SingleLiveEvent<Triple<List<BottomSheetItem>,String,Int?>>()
    val reportMessageList :LiveData<Triple<List<BottomSheetItem>,String,Int?>> get() = _reportMessageList
    var reportIdList :List<Int> = listOf()
    fun getReportMessageList(typeOfReport :String,replyId: Int?){
        viewModelScope.launch{
            detailPostRepository.getReportMessageList().run {
                _reportMessageList.value=Triple(
                    this.map {
                        BottomSheetItem(null,it.message,null,null,null)
                    },
                    typeOfReport,
                    replyId
                )
                reportIdList=this.map {
                    it.reportMessageId
                }
            }
        }
    }

    //클럽 게시글 상세 불러오기
    private val _bookMarkExists =MutableLiveData<Boolean>()
    val bookMarkExists :LiveData<Boolean> get() = _bookMarkExists
    fun getClubPostData(clubId: String,categoryCode : String, postId: Int) {
        viewModelScope.launch {
            val detailResult =
                detailPostRepository.getClubPostDetailItem(accessToken, uId,clubId,categoryCode,postId)
            if (detailResult.second != null) {
                _detailPostErrorLiveData.value=detailResult.second!!.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
            } else if (detailResult.first != null) {
                val postItem =detailResult.first!!
                _postToolbarNameLiveData.value = postItem.categoryName1
                postData = listOf(
                    DetailPostItem.ClubPostDetail(
                        ConstVariable.TYPE_CLUB,
                        postItem,
                    )
                )
                if(postItem.deleteTyep==1){
                    _bookMarkExists.value=false
                }else if(postItem.deleteTyep==2){
                    _bookMarkExists.value=true
                }
//                _postDetailDataLiveData.value = postData
                getClubCommentList(clubId,categoryCode,postId,"init")
            }
        }
    }

    //클럽 게시글 북마크 여부 확인
    fun getClubPostBookMark(clubId: String, categoryCode : String, postId: Int){
        viewModelScope.launch {
            if(accessToken!=null && uId !=null){
                detailPostRepository.fetchClubPostBookMarkYn(clubId,categoryCode,postId,uId!!).run {
                    _bookmarkClickResultLiveData.value=this.first!!
                    isMember=true
                    if(this.second!=null){
                        if(this.second!!.code==ConstVariable.ERROR_FE3000){
                            isMember=false
                        }
                    }
                    _bookMarkExists.value=isMember
                }
            }
        }
    }
    //클럽 북마크 변경
    fun changeClubPostBookmarkYn(clubId: String, categoryCode : String, postId: Int){
        viewModelScope.launch {
            if(accessToken!=null && uId !=null){
                detailPostRepository.patchClubBookmarkYn(clubId,categoryCode,postId,uId!!).run {
                    _bookmarkClickResultLiveData.value=this
                }
            }
        }
    }
    //번역
    private val _translatePostData = SingleLiveEvent<Pair<Boolean,DetailPostItem.ClubPostDetail>>()
    val translatePostData: LiveData<Pair<Boolean,DetailPostItem.ClubPostDetail>> get() = _translatePostData
    private var originMessage = listOf<TransMessage>()
    fun clickTranslatePost(transText: List<String>, isTranslate :Boolean) {
        val item =postData[0]
        viewModelScope.launch {
            if(isTranslate){
                when(item){
                    is DetailPostItem.ClubPostDetail->{
                        originMessage.forEach {
                            if(item.item.subject==it.text)item.item.subject=it.origin
                            if(item.item.content==it.text)item.item.content=it.origin
                        }
                        _translatePostData.value= Pair(false,item)
                    }
                    else->{}
                }
            }else{
                translateRepository.translateWords(
                    transText,
                    dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)?:"en").run {
                    this.first?.let {
                        originMessage=it
                        when(item){
                            is DetailPostItem.ClubPostDetail->{
                                originMessage.forEach { transMessage->
                                    if(item.item.subject==transMessage.origin)item.item.subject=transMessage.text
                                    if(item.item.content==transMessage.origin)item.item.content=transMessage.text
                                }
                                _translatePostData.value= Pair(true,item)
                            }
                            else->{}
                        }
                    }
                    this.second?.let {
                        _detailPostErrorLiveData.value=it.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                    }
                }
            }
        }
    }

    //댓글 nextId 초기화
    fun setInitReplyNextId() {
        replayNextId = null
    }

    //모든 댓글 리스트 받아오기
    fun getClubCommentAllList(clubId: String,categoryCode :String,postId :Int,type : String){
        viewModelScope.launch {
            val callRes = detailPostRepository.fetchClubCommentList(
                uId,accessToken,clubId,categoryCode,postId,replayNextId,30)

            if (callRes.third != null) {

            } else if (callRes.first != null) {
                if (postData.isNotEmpty()) {
                    if (type == "init") {
                        commentList.clear()
                        commentSize=0
                    }
                    //childReply 를 좀 더 view에 보여주기 쉬운 형태로 변형
                    setChildReplyItem(callRes.first).forEach {
                        commentList.add(DetailPostItem.ClubPostComment("Comment", it))
                    }
                    commentSize += (callRes.first?.size ?: 0)
                    replayNextId = callRes.second
                    if(replayNextId !="-1"){
                        getClubCommentAllList(clubId, categoryCode, postId, "Add")
                    }else{
                        _postDetailDataLiveData.value = postData + commentList
                    }
                }
            }
        }
    }

    //클럽 댓글 리스트 받아오기
    private var commentSize = 0
    fun getClubCommentList(clubId: String,categoryCode :String,postId :Int,type : String){
        viewModelScope.launch {
            if(replayNextId=="-1") return@launch
            val size = if(commentSize<10 || type=="Add") 10 else commentSize
            val callRes = detailPostRepository.fetchClubCommentList(
                uId,accessToken,clubId,categoryCode,postId,replayNextId,size)

            if (callRes.third != null) {
                _postDetailDataLiveData.value = postData
            } else if (callRes.first != null) {
                if (postData.isNotEmpty()) {
                    if (type == "init") {
                        commentList.clear()
                        commentSize=0
                    }
                    commentSize += (callRes.first?.size ?: 0)
                    //childReply 를 좀 더 view에 보여주기 쉬운 형태로 변형
                    setChildReplyItem(callRes.first).forEach {
                        commentList.add(DetailPostItem.ClubPostComment("Comment", it))
                    }
                    _postDetailDataLiveData.value = postData + commentList
                }
                replayNextId = callRes.second!!
            }
        }
    }

    //대댓글 상태로 변경
    private fun setChildReplyItem(originalReplyList : List<PostReplyData>?) :List<ClubReplyData> {
        val replayList : ArrayList<ClubReplyData> = arrayListOf()
        originalReplyList?.forEach {
            if(it is ClubReplyData){
                if(it.depth==1){
                    replayList.add(it)
                }else{
                    replayList.map { clubReplyItem->
                        if(clubReplyItem.replyId==it.parentReplyId){
                            if(clubReplyItem.childReplyList==null){
                                clubReplyItem.childReplyList= arrayListOf(it)
                            }else{
                                clubReplyItem.childReplyList?.add(it)
                            }
                        }
                        return@map clubReplyItem
                    }
                }
            }
        }
        return replayList
    }

    //댓글 입력 상태 구분
    fun setCommentContent(text : String?){
        _commentContent.value=text
    }

    //댓글 입력
    private val _resultCommentInput =SingleLiveEvent<Boolean>()
    val resultCommentInput :LiveData<Boolean> =_resultCommentInput
    var commentText =""
    fun sendPostComment(clubId: String, categoryCode : String, postId : Int,imageKey:String?){
        viewModelScope.launch {
            if(accessToken==null || uId ==null){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            var sendAttachList:JsonObject? =null
            if(imageKey!=null){
                sendAttachList = JsonObject().apply {
                    addProperty("attach", imageKey)
                    addProperty("attachType", 0)
                }
            }
            detailPostRepository.callSendClubCommentData(
                clubId, categoryCode, postId,
                HashMap<String,Any>().apply {
                    if(sendAttachList!=null)this["attachList"]= listOf(sendAttachList)
                    this["content"]=commentText
                    this[ConstVariable.KEY_UID]=uId!!
                    this["langCode"]=dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)?:"en"
                }
            ).run {
                _resultCommentInput.value=this==ConstVariable.RESULT_SUCCESS_CODE
                if(this!=ConstVariable.RESULT_SUCCESS_CODE){
                    _detailPostErrorLiveData.value=this
                }
                commentText=""
            }
        }
    }
    //댓글 수정
    private val _resultModifyComment =SingleLiveEvent<Boolean>()
    val resultModifySomment :LiveData<Boolean> =_resultModifyComment
    fun sendPostModifyComment(clubId: String, categoryCode : String, postId : Int,replyId:Int,imageKey:String?){
        viewModelScope.launch {
            if(!isLoginUser){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            var sendAttachList:JsonObject? =null
            if(imageKey!=null){
                sendAttachList = JsonObject().apply {
                    addProperty("attach", imageKey)
                    addProperty("attachType", 0)
                }
            }
            detailPostRepository.callModifyClubCommentData(
                clubId, categoryCode, postId,replyId,
                HashMap<String,Any>().apply {
                    if(sendAttachList!=null)this["attachList"]= listOf(sendAttachList)
                    else this["attahList"]= listOf<JsonObject>()
                    this["content"]=commentText?:""
                    this[ConstVariable.KEY_UID]=uId!!
                    this["langCode"]=dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)?:"en"
                }
            ).run {
                if(this==ConstVariable.RESULT_SUCCESS_CODE){
                    _resultModifyComment.value=true
                }else{
                    _detailPostErrorLiveData.value=this
                }
            }
        }
    }

    //이미지 전송
    fun sendImageToCloudFlare(cloudFlareKey: String) {
        viewModelScope.launch {
            if(multipartBody==null){
                _detailPostErrorLiveData.value = ConstVariable.ERROR_IMAGE_UPLOAD_FAIL
            }
            multipartBody?.let {
                uploadRepository.fileUploadImageToCloudFlare(cloudFlareKey, it).run {
                    if (this == null) {
                        _detailPostErrorLiveData.value = ConstVariable.ERROR_WAIT_FOR_SECOND
                    } else {
                        _cloudFlareUploadLiveData.value = this
                    }
                }
            }
        }
    }

    //이미지 셋팅
    fun addOnImageList(imageUrl: Uri?,imageFile : MultipartBody.Part) {
        _imageUrlLiveData.value = imageUrl
        multipartBody=imageFile
    }
    //이미지 초기화
    fun setInitAttachImaga(){
        _imageUrlLiveData.value = null
        attachItem = null
        multipartBody=null
    }

    //댓글 삭제
    fun clickDeleteComment( clubId: String,categoryCode :String,postId :Int,replyId :Int){
        viewModelScope.launch {
            if(accessToken == null || uId ==null){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.deleteClubComment(
                clubId,categoryCode,postId,replyId,
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId!!
                    this["reasonMsh"]="이유"
                }
            ).run {
                _resultModifyComment.value=this==ConstVariable.RESULT_SUCCESS_CODE
                if(this!=ConstVariable.RESULT_SUCCESS_CODE){
                    _detailPostErrorLiveData.value=this?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
            }
        }
    }

    //댓글 신고
    private val _reportCommentResultLiveData = MutableLiveData<Boolean>()
    val reportCommentResultLiveData: LiveData<Boolean> get() = _reportCommentResultLiveData
    fun clickReportClubComment(pos: Int, clubId: String, categoryCode: String, postId: Int, replyId: Int) {
        viewModelScope.launch {
            if(uId==null){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.reportClubComment(
                clubId,
                categoryCode,
                postId,
                replyId,
                uId!!,
                reportIdList[pos]
            ).run {
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    //신고 성공
                    _reportCommentResultLiveData.value = true
                }else{
                    _detailPostErrorLiveData.value=this
                }
            }
        }
    }

    //댓글 차단
    private val _blockCommentResultLiveData = MutableLiveData<Boolean>()
    val blockCommentResultLiveData: LiveData<Boolean> get() = _blockCommentResultLiveData
    fun clickBlockComment(clubId: String,categoryCode: String,postId :Int,replyId:Int) {
        viewModelScope.launch {
            if(accessToken==null || uId == null ){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.patchBlockClubComment(
                uId!!,
                clubId,
                categoryCode,
                postId,
                replyId
            ).run{
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _blockCommentResultLiveData.value=true
                }else{
                    _detailPostErrorLiveData.value=this
                }
            }
        }
    }

    //댓글 번역
    private val _transCommentReply = SingleLiveEvent<Pair<Boolean,String>>()
    val transCommentReply : LiveData<Pair<Boolean,String>> get() = _transCommentReply
    private val commentHashMap =HashMap<Int,HashMap<Int,String>>()
    fun clickTransComment(originText :String,pos1: Int,pos2:Int,isTranslate: Boolean){
        viewModelScope.launch {
            val secondPos =if(pos1==pos2) 0 else pos2
            if(isTranslate){
                if(commentHashMap[pos1]!=null){
                    _transCommentReply.value=Pair(false,commentHashMap[pos1]!![secondPos]!!)
                }
            }else{
                if(commentHashMap[pos1]==null){
                    commentHashMap[pos1]=HashMap<Int,String>().apply {
                        this[secondPos]=originText
                    }
                }else{
                    commentHashMap[pos1]!![secondPos] = originText
                }
                translateRepository.translateWords(
                    listOf(originText),
                    dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE) ?: "en"
                ).run {
                    this.first?.let {
                        if(it.isNotEmpty()){
                            _transCommentReply.value=Pair(true,it[0].text)
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
    private val _blockPostResultLiveData = MutableLiveData<Boolean>()
    val blockPostResultLiveData: LiveData<Boolean> get() = _blockPostResultLiveData
    fun clickBlockClubPost(clubId: String,categoryCode: String,postId: Int)  {
        viewModelScope.launch {
            if(accessToken==null || uId == null ){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.patchBlockClubPost(clubId,categoryCode,postId,uId!!).run{
                if(this.first !=null){
                    _blockPostResultLiveData.value=this.first!!
                }else{
                    _detailPostErrorLiveData.value=this.second?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
            }
        }
    }
    //게시글 삭제
    private val _deletePostResultLiveData = SingleLiveEvent<Boolean>()
    val deletePostResultLiveData :LiveData<Boolean> get() = _deletePostResultLiveData
    fun clickDeleteClubPost(clubId: String,categoryCode: String,postId: Int,reasonMsg:String?){
        viewModelScope.launch {
            if(!isLoginUser){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.patchDeleteClubPost(clubId, categoryCode, postId, uId!!,reasonMsg).run {
                if(this.first){
                    _deletePostResultLiveData.value=this.first!!
                }
                if(this.second!=null){
                    _detailPostErrorLiveData.value=this.second?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
            }
        }
    }

    //클럽 사용자 차단
    private val _blockAccountResultLiveData = MutableLiveData<Boolean>()
    val blockAccountResultLiveData: LiveData<Boolean> get() = _blockAccountResultLiveData
    fun clickBlockAccountComment(clubId: String,memberId:Int) {
        viewModelScope.launch {
            if(accessToken==null || uId == null ){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.patchClubMemberBlock(clubId, memberId, uId!!).run {
                if(this ==ConstVariable.RESULT_SUCCESS_CODE){
                    _blockAccountResultLiveData.value=true
                }else{
                    _detailPostErrorLiveData.value=this
                }
            }
        }
    }
    //클럽 게시글 신고
    private val _reportClubPostData = SingleLiveEvent<Boolean>()
    val reportClubPostData :LiveData<Boolean> get() = _reportClubPostData
    fun clickReportClubPost(clickPos: Int,clubId: String,categoryCode: String,postId: Int) {
        viewModelScope.launch {
            if(accessToken==null || uId ==null){
                _detailPostErrorLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            detailPostRepository.callReportClubPost(
                clubId,
                categoryCode,
                postId,
                uId!!,
                reportIdList[clickPos]
            ).run {
                if(this==ConstVariable.RESULT_SUCCESS_CODE){
                    //신고 완료
                    _reportClubPostData.value=true
                }else{
                    //신고 실패
                    _reportClubPostData.value=false
                    _detailPostErrorLiveData.value=this
                }
            }
        }
    }

    fun requestClubNoticeDetailItem(categoryCode: String,clubId: String, postId: Int) {
        viewModelScope.launch {
            val safeRes = detailPostRepository.getClubNoticeDetailData(uId,accessToken, categoryCode,clubId, postId)
            if (safeRes.second != null) {

            } else if (safeRes.first != null) {
                val noticeItem =safeRes.first!!
                _postToolbarNameLiveData.value = ""
                postData = listOf(
                    DetailPostItem.ClubPostDetail(
                        ConstVariable.TYPE_CLUB,
                        noticeItem,
                    )
                )
                _postDetailDataLiveData.value = postData
                getClubCommentList(clubId,categoryCode,postId,"init")
            }
        }
    }

    fun getClubChallengePostDetailData(postId: Int){
        viewModelScope.launch {
            detailPostRepository.getClubChallengeDetailData(uId,postId).run {
                if(this.first!=null){
                    postData=listOf(
                        DetailPostItem.ClubPostDetail(
                            ConstVariable.TYPE_CLUB_CHALLENGE,
                            this.first!!
                        )
                    )
                    _postDetailDataLiveData.value= postData
//                    getClubChallengeCommentList(postId,"init")
                }else{
                    _detailPostErrorLiveData.value=this.second!!.code!!
                }
            }
        }
    }
    //챌린지 댓글[삭제 예정]
    fun getClubChallengeCommentList(postId :Int,type : String){
        viewModelScope.launch {
            val size = if(commentList.size<10) 10 else commentList.size
            val callRes = detailPostRepository.fetchClubChallengeCommentList(
                uId,postId,replayNextId,size)

            if (callRes.third != null) {

            } else if (callRes.first != null) {
                if (postDetailDataLiveData.value != null) {
                    if (type == "init") {
                        commentList.clear()
                    }
                    //childReply 를 좀 더 view에 보여주기 쉬운 형태로 변형
                    setChildReplyItem(callRes.first).forEach {
                        commentList.add(DetailPostItem.ClubPostComment("Comment", it))
                    }
                    _postDetailDataLiveData.value = postData + commentList
                    replayNextId = callRes.second!!
                }
            }
        }
    }

}