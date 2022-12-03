package com.rndeep.fns_fantoo.ui.club.post

import com.google.gson.JsonObject
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.PostDetailService
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.data.remote.model.ReportMessageItem
import com.rndeep.fns_fantoo.data.remote.model.community.BoardPostDetailResponse
import com.rndeep.fns_fantoo.data.remote.model.community.ClubPostDetailData
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeDetailResponse
import com.rndeep.fns_fantoo.data.remote.model.community.LikeDislikeResult
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import javax.inject.Inject

class DetailPostRepository @Inject constructor(
    @NetworkModule.ApiServer private val postDetailService: PostDetailService,
) : BaseNetRepo() {

    //커뮤니티 게시글
    suspend fun getCommunityPostDetail(
        accessToken: String?,
        uId: String?,
        boardCode: String,
        postId: Int
    ): Pair<BoardPostDetailResponse?, ErrorBody?> {
        val postDetailRes = if (accessToken == null || uId == null)
        //비회원용(게시글 상세)
            safeApiCall(Dispatchers.IO) {
                postDetailService.getCommunityPostDetail(boardCode, postId.toString())
            }
        else
        //회원용(게시글 상세)
            safeApiCall(Dispatchers.IO) {
                postDetailService.getCommunityPostDetail(
                    accessToken,
                    boardCode,
                    postId.toString(),
                    uId
                )
            }

        return when (postDetailRes) {
            is ResultWrapper.Success -> {
                Pair(postDetailRes.data, null)
            }
            is ResultWrapper.GenericError -> {
                Pair(
                    null,
                    ErrorBody(postDetailRes.code, postDetailRes.message, postDetailRes.errorData)
                )
            }
            is ResultWrapper.NetworkError -> {
                Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error!", null))
            }
        }
    }

    //공지 상세 게시글[커뮤니티]
    suspend fun getCommunityNoticeDetailData(
        boardCode: String?,
        postId: Int
    ): Pair<CommunityNoticeDetailResponse?, ErrorBody?> {
        val noticeRes = safeApiCall(Dispatchers.IO) {
            if (boardCode == null || boardCode == "") {
                postDetailService.getNoticeDetailItem(postId)
            } else {
                postDetailService.getNoticeDetailItem(boardCode, postId)
            }
        }

        return when (noticeRes) {
            is ResultWrapper.Success -> {
                Pair(noticeRes.data, null)
            }
            is ResultWrapper.GenericError -> {
                Pair(null, ErrorBody(noticeRes.code, noticeRes.message, noticeRes.errorData))
            }
            is ResultWrapper.NetworkError -> {
                Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error! ", null))
            }
        }
    }
    //댓글 리스트[커뮤니티]
    suspend fun getPostCommentList(
        accessToken: String?,
        uId: String?,
        postId: String,
        nextId: Int?,
        size: Int,
        isLogin :Boolean
    ): Triple<List<CommunityReplyData>?, Int?, ErrorBody?> {
        //postId 110 에만 더미 데이터 존재
        val commentRes = if (!isLogin) {
            safeApiCall(Dispatchers.IO) {
                postDetailService.getCommunityCommentListInfo(
                    postId,
                    nextId,
                    size
                )
            }
        } else {
            safeApiCall(Dispatchers.IO) {
                postDetailService.getCommunityCommentListInfo(
                    accessToken!!,
                    postId,
                    uId!!,
                    nextId,
                    size,
                )
            }
        }
        return when (commentRes) {
            is ResultWrapper.Success -> {
                val replyData = commentRes.data
                Triple(replyData.reply, replyData.nextId, null)
            }
            is ResultWrapper.GenericError -> {
                Triple(
                    null,
                    null,
                    ErrorBody(commentRes.code, commentRes.message, commentRes.errorData)
                )
            }
            is ResultWrapper.NetworkError -> {
                Triple(null, null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error", null))
            }
        }
    }
    //게시글 상세[클럽]
    suspend fun getClubPostDetailItem(
        accessToken: String?,
        uId: String?,
        clubId: String,
        categoryCode: String,
        postId:Int
    ): Pair<ClubPostDetailData?,ErrorBody?>{
        safeApiCall(Dispatchers.IO){
            if(accessToken==null || uId == null){
                postDetailService.getClubPostDetail(categoryCode,clubId,postId)
            }else{
                postDetailService.getClubPostDetail(accessToken,categoryCode,clubId,postId,uId)
            }
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data,null)
                }
                is ResultWrapper.GenericError->{
                    Pair(null,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Pair(null,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }

    }
    //댓글  좋아요,싫어요 선택 또는 취소[커뮤니티]
    suspend fun callCommunityLickDisLikeClick(
        accessToken: String,
        isCancel: Boolean,
        likeType: String,
        targetType: String,
        targetId: String,
        hashBody: HashMap<String, Any>
    ): Pair<LikeDislikeResult?, ErrorBody?> {
        val res = if (isCancel) safeApiCall(Dispatchers.IO) {
            postDetailService.cancelPostLike(
                accessToken,
                targetType,
                targetId,
                hashBody
            )
        }
        else safeApiCall(Dispatchers.IO) {
            postDetailService.clickPostCommentLike(
                accessToken,
                likeType,
                targetType,
                targetId,
                hashBody
            )
        }

        res.run {
            return when (this) {
                is ResultWrapper.Success -> {
                    Pair(this.data, null)
                }
                is ResultWrapper.GenericError -> {
                    Pair(null, ErrorBody(this.code, this.message, this.errorData))
                }
                is ResultWrapper.NetworkError -> {
                    Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, "NetWork Error", null))
                }
            }
        }
    }


    //Honor 선책[커뮤니티]
    suspend fun callHonorClick() {
        postDetailService.clickPostHonor()
    }

    //게시글 북마크[커뮤니티]
    suspend fun callBookmarkPostClick(accessToken: String, postId: Int, integUid: String) =
        safeApiCall(Dispatchers.IO) {
            postDetailService.callRegisterBookmark(
                accessToken,
                postId.toString(),
                HashMap<String, Any>().apply {
                    this[ConstVariable.KEY_UID] = integUid
                })
        }

    //게시글 북마크 해제[커뮤니티]
    suspend fun callUnBookmarkPostClick(accessToken: String, postId: Int, integUid: String) =
        safeApiCall(Dispatchers.IO) {
            postDetailService.callUnRegisterBookmark(
                accessToken,
                postId.toString(),
                HashMap<String, Any>().apply {
                    this[ConstVariable.KEY_UID] = integUid
                })
        }

    //댓글 신고하기[커뮤니티]
    suspend fun callReportCommunityPostComment() {
        postDetailService.reportComment()
    }
    //댓글 삭제[커뮤니티]
    suspend fun deleteComment(
        accessToken: String,
        uId: String,
        postId: String,
        replyId: String
    ): String? {
        val body = HashMap<String, String>()
        body[ConstVariable.KEY_UID] = uId
        val res = safeApiCall(Dispatchers.IO) {
            postDetailService.deleteCommunityPostComment(
                accessToken,
                postId,
                replyId,
                body
            )
        }

        return when (res) {
            is ResultWrapper.Success -> {
                ConstVariable.RESULT_SUCCESS_CODE
            }
            is ResultWrapper.GenericError -> {
                res.code
            }
            is ResultWrapper.NetworkError -> {
                ConstVariable.ERROR_NETWORK
            }
        }
    }

    //댓글 수정[커뮤니티]
    suspend fun modifyComment(
        accessToken: String,
        uId: String,
        postId: String,
        comment: String,
        anonymYn: Boolean,
        attachType: String?,
        attachId: String?,
        replyId: String
    ): String? {
        val sendAttachList = JsonObject().apply {
            addProperty("attachType", attachType)
            addProperty("id", attachId)
        }
        val jsonList = JSONArray()
        jsonList.put(sendAttachList)
        val res = safeApiCall(Dispatchers.IO) {
            postDetailService.modifyPostComment(
                accessToken,
                postId.toString(),
                replyId,
                HashMap<String, Any>().apply {
                    this["anonymYn"] = anonymYn
                    this["content"] = comment
                    this[ConstVariable.KEY_UID] = uId
                    if (attachType != null) this["attachList"] = listOf(sendAttachList)
                })
        }

        return when (res) {
            is ResultWrapper.Success -> {
                ConstVariable.RESULT_SUCCESS_CODE
            }
            is ResultWrapper.GenericError -> {
                res.code
            }
            is ResultWrapper.NetworkError -> {
                ConstVariable.ERROR_NETWORK
            }
        }
    }
    //계정 차단[커뮤니티]
    suspend fun callBlockCommunityAccount(
        accessToken: String,
        uId: String,
        targetUid: String,
        isBlock: Boolean
    ): String {
        safeApiCall(Dispatchers.IO) {
            if (isBlock) {
                postDetailService.unblockUserPost(
                    accessToken,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uId
                        this[ConstVariable.KEY_TARGET_UID] = targetUid
                    }
                )
            } else {
                postDetailService.blockUserPost(
                    accessToken,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uId
                        this[ConstVariable.KEY_TARGET_UID] = targetUid
                    }
                )
            }
        }.run {
            return when (this) {
                is ResultWrapper.Success -> {
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError -> {
                    this.code ?: ConstVariable.ERROR_AUTH
                }
                is ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    //게시글 차단 및 해제[커뮤니티]
    suspend fun callBlockPostComment(
        accessToken: String,
        targetType : String,
        targetCommentId: Int,
        myUid: String,
        targetUId: String,
        isBlock: Boolean
    ): String? {
        safeApiCall(Dispatchers.IO) {
            if (isBlock) {
                postDetailService.unblockPiecePost(
                    accessToken,
                    targetType,
                    targetCommentId.toString(),
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = myUid
                        this[ConstVariable.KEY_TARGET_UID] = targetUId
                    }
                )
            } else {
                postDetailService.blockPiecePost(
                    accessToken,
                    targetType,
                    targetCommentId.toString(),
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = myUid
                        this[ConstVariable.KEY_TARGET_UID] = targetUId
                    }
                )
            }
        }.run {
            return when (this) {
                is ResultWrapper.Success -> {
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError -> {
                    this.code
                }
                is ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    //게시글/댓글 신고하기[커뮤니티]
    suspend fun reportCommunityPost(
        targetId: Int,
        type: String,
        uId: String,
        targetUId: String,
        reportId: Int
    ) :String {
        safeApiCall(Dispatchers.IO) {
            postDetailService.reportCommunityPostComment(
                targetId,
                type,
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                    this[ConstVariable.KEY_TARGET_UID]=targetUId
                    this["reportMessageId"]=reportId
                }
            )
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError->{
                    this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                is ResultWrapper.NetworkError->{
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    //게시글 삭제[커뮤니티]
    suspend fun updateDeletePost(
        accessToken: String, uId: String, code: String, postId: String
    ): Pair<Boolean, String> {
        safeApiCall(Dispatchers.IO) {
            postDetailService.deleteMyPost(
                accessToken,
                code,
                postId,
                HashMap<String, Any>().apply { this[ConstVariable.KEY_UID] = uId }
            )
        }.run {
            return when (this) {
                is ResultWrapper.Success -> {
                    Pair(true,ConstVariable.RESULT_SUCCESS_CODE)
                }
                is ResultWrapper.GenericError -> {
                    Pair(false, this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND )
                }
                is ResultWrapper.NetworkError -> {
                    Pair(false, ConstVariable.ERROR_NETWORK)
                }
            }
        }
    }

    //댓글 입력[커뮤니티]
    suspend fun callSendCommunityCommentData(
        accessToken: String,
        uId: String,
        postId: Int,
        comment: String,
        anonymYn: Boolean,
        attachType: String?,
        attachId: String?
    ): String? {
        val sendAttachList = JsonObject().apply {
            addProperty("attachType", attachType)
            addProperty("id", attachId)
        }
        val jsonList = JSONArray()
        jsonList.put(sendAttachList)
        val res = safeApiCall(Dispatchers.IO) {
            postDetailService.sendPostComment(
                accessToken,
                postId.toString(),
                HashMap<String, Any>().apply {
                    this["anonymYn"] = anonymYn
                    this["content"] = comment
                    this[ConstVariable.KEY_UID] = uId
                    if (attachType != null) this["attachList"] = listOf(sendAttachList)
                })
        }

        return when (res) {
            is ResultWrapper.Success -> {
                ConstVariable.RESULT_SUCCESS_CODE
            }
            is ResultWrapper.GenericError -> {
                res.code
            }
            is ResultWrapper.NetworkError -> {
                ConstVariable.ERROR_NETWORK
            }
        }
    }
    //공지 상세글[클럽]
    suspend fun getClubNoticeDetailData(
        uId: String?,
        accessToken: String?,
        categoryCode: String,
        clubId: String,
        postId: Int
    ) : Pair<ClubPostDetailData?,ErrorBody?>{
        safeApiCall(Dispatchers.IO){
            if(uId==null || accessToken == null){
                postDetailService.getClubPostDetail(categoryCode,clubId,postId)
            }else{
                postDetailService.getClubPostDetail(accessToken,categoryCode,clubId,postId,uId)
            }
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data,null)
                }
                is ResultWrapper.GenericError->{
                    Pair(null,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Pair(null,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }

    //클럽 댓글 리스트
    suspend fun fetchClubCommentList(
        uId: String?,
        accessToken: String?,
        clubId: String,
        categoryCode : String,
        postId : Int,
        nextId : String?,
        size : Int
    ) :Triple<List<PostReplyData>?, String, ErrorBody?> {
        safeApiCall(Dispatchers.IO){
            if(uId==null || accessToken==null){
                postDetailService.getClubCommentListInfo(
                    clubId,categoryCode,postId,nextId,size
                )
            }else{
                postDetailService.getClubCommentListInfo(
                    accessToken,clubId,categoryCode,postId,uId,nextId,size
                )
            }
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    if(this.data.reply.isEmpty()){
                        Triple(this.data.reply,"-1",null)
                    }else{
                        Triple(this.data.reply,this.data.nextId,null)
                    }
                }
                is ResultWrapper.GenericError->{
                    Triple(null,"-1",ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Triple(null,"-1",null)
                }
            }
        }
    }
    //쿨롭 게시글 북마크 확인
    suspend fun fetchClubPostBookMarkYn(clubId: String, categoryCode :String, postId :Int, uId :String) :Pair<Boolean,ErrorBody?>{
        safeApiCall(Dispatchers.IO){
            postDetailService.getClubPostBookMarkYn(clubId,categoryCode,postId,uId)
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data.bookmarkYn,null)
                }
                is ResultWrapper.GenericError ->{
                    Pair(false,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Pair(false,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }

    //클럽 게시글 북마크 변경
    suspend fun patchClubBookmarkYn(clubId: String,categoryCode :String,postId :Int,uId :String) : Boolean{
        safeApiCall(Dispatchers.IO){
            postDetailService.patchClubPostBookMarkYn(clubId,categoryCode,postId,HashMap<String,Any>().apply {
                this[ConstVariable.KEY_UID]=uId
            })
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.bookmarkYn
                }
                else ->{
                    false
                }
            }
        }
    }

    //챌린지 게시글 상세
    suspend fun getClubChallengeDetailData(
        uId: String?,
        postId: Int
    ) : Pair<ClubPostDetailData?,ErrorBody?> {
        safeApiCall(Dispatchers.IO){
            postDetailService.getChallengePostDetail(postId,uId?:"guest")
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data,null)
                }
                is ResultWrapper.GenericError->{
                    Pair(null,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Pair(null,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }
    //클럽 챌린지 댓글 리스트
    suspend fun fetchClubChallengeCommentList(
        uId: String?,
        postId: Int,
        nextId: String?,
        size: Int
    ) : Triple<List<ClubReplyData>?, String?, ErrorBody?> {
        safeApiCall(Dispatchers.IO){
            postDetailService.fetchClubChallengeComment(
                postId,uId?:"guest",nextId,size
            )
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    Triple(this.data.reply,this.data.nextId,null)
                }
                is ResultWrapper.GenericError->{
                    Triple(null,"-1",ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Triple(null,"-1",ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }
    //댓글 입력[클럽]
    suspend fun callSendClubCommentData(
        clubId: String,
        categoryCode: String,
        postId: Int,
        bodyHashMap: HashMap<String,Any>
    ) : String {
        safeApiCall(Dispatchers.IO){
            postDetailService.registerClubComment(
                clubId,
                categoryCode,
                postId,
                bodyHashMap
            )
        }.run {
           return when(this){
                is ResultWrapper.Success -> {
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError -> {
                    this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }
    //댓글 수정[클럽]
    suspend fun callModifyClubCommentData(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId :Int,
        bodyHashMap: HashMap<String,Any>
    ) : String{
        safeApiCall(Dispatchers.IO){
            postDetailService.modifyClubComment(
                clubId,categoryCode,postId,replyId,bodyHashMap
            )
        }.run {
            return when(this){
                is ResultWrapper.Success -> {
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError -> {
                    this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                is ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }
    //댓글 삭제 [클럽]
    suspend fun deleteClubComment(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId :Int,
        bodyHashMap: HashMap<String,Any>
    ):String?{
        safeApiCall(Dispatchers.IO){postDetailService.deleteClubPostComment(
            clubId,categoryCode,postId,replyId,bodyHashMap
        )}.run {
           return when(this){
                is ResultWrapper.Success -> {
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError -> {
                    this.code
                }
                ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_WAIT_FOR_SECOND
                }
            }
        }
    }

    //댓글 차단 [클럽]
    suspend fun patchBlockClubComment(
        uId: String,
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int
    ): String {
        safeApiCall(Dispatchers.IO) {
            postDetailService.patchBlockClubComment(
                clubId,
                categoryCode,
                postId,
                replyId,
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID] = uId
                }
            )}.run {
            return when(this){
                is ResultWrapper.Success->{
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError->{
                    this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                is ResultWrapper.NetworkError->{
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    //댓글 신고 [클럽]
    suspend fun reportClubComment(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int,
        uId: String,
        reportId: Int
    ) :String {
        safeApiCall(Dispatchers.IO){
            postDetailService.reportClubComment(
                clubId,
                categoryCode,
                postId,
                replyId,
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                    this["reportMessageId"]=reportId
                }
            )
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError->{
                    this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                is ResultWrapper.NetworkError->{
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    //게시글 차단 [클럽]
    suspend fun patchBlockClubPost(clubId: String,categoryCode: String,postId: Int,uId: String) : Pair<Boolean?,ErrorBody?>{
        safeApiCall(Dispatchers.IO){
            postDetailService.patchBlockClubPost(
                clubId,
                categoryCode,
                postId,
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                }
            )
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data.blockYn,null)
                }
                is ResultWrapper.GenericError -> {
                    Pair(null,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Pair(null,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }
    //멤버 차단 [클럽]
    suspend fun patchClubMemberBlock(clubId: String,memberId :Int,uId: String) :String{
        safeApiCall(Dispatchers.IO){postDetailService.patchBlockClubMember(clubId,memberId,HashMap<String,Any>().apply {
            this[ConstVariable.KEY_UID]=uId
        })}.run {
            return when(this){
                is ResultWrapper.Success->{
                   ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError->{
                    this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                is ResultWrapper.NetworkError->{
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    //게시글 삭제[클럽]
    suspend fun patchDeleteClubPost(
        clubId: String,
        categoryCode: String,
        postId: Int,
        uId: String,
        reasonMsg: String?
    ): Pair<Boolean, ErrorBody?> {
        safeApiCall(Dispatchers.IO) {
            postDetailService.patchDeleteClubPost(
                clubId,
                categoryCode,
                postId,
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                    if(reasonMsg!=null) this["reasonMsg"]=reasonMsg
                }
            )
        }.run {
            return when(this){
                is ResultWrapper.Success -> {
                    Pair(true,null)
                }
                is ResultWrapper.GenericError -> {
                    Pair(false,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError -> {
                    Pair(false,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }
    //게시글 신고 리스트 얻게
    suspend fun getReportMessageList() :List<ReportMessageItem>{
        safeApiCall(Dispatchers.IO){
            postDetailService.getReportMessages()
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.reportList
                }
                is ResultWrapper.GenericError->{
                    listOf()
                }
                is ResultWrapper.NetworkError->{
                    listOf()
                }
            }
        }
    }

    //게시글 신고 하기[클럽]
    suspend fun callReportClubPost(
        clubId: String,
        categoryCode: String,
        postId: Int,
        uId: String,
        reportId: Int
    ) :String{
        safeApiCall(Dispatchers.IO) {
            postDetailService.reportClubPost(
                clubId,
                categoryCode,
                postId,
                HashMap<String, Any>().apply {
                    this[ConstVariable.KEY_UID] = uId
                    this["reportMessageId"] = reportId
                }
            )
        }.run {
            return when(this){
                is ResultWrapper.Success -> {
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError -> {
                    this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

}