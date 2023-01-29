package com.rndeep.fns_fantoo.ui.community.comment

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.PostDetailService
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.ReportMessageItem
import com.rndeep.fns_fantoo.data.remote.model.community.LikeDislikeResult
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CommunityCommentRepository @Inject constructor(
    @NetworkModule.ApiServer private val postDetailService: PostDetailService
) : BaseNetRepo() {

    //신고 메세지 리스트[공통]
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

    //댓글 리스트[커뮤니티]
    suspend fun callCommunityComment(
        accessToken: String?,
        isLogin : Boolean,
        uid: String?,
        postId: Int,
        replyId: Int,
        nextId: String?,
        size :Int
    ): Triple<List<CommunityReplyData>?, String?, ErrorBody?> {
        val safeRes = if (!isLogin) {
            safeApiCall(Dispatchers.IO) {
                postDetailService.getCommentDetailInfo(postId, replyId, nextId, size)
            }
        } else {
            safeApiCall(Dispatchers.IO) {
                postDetailService.getCommentDetailInfo(
                    accessToken!!,
                    postId,
                    replyId,
                    uid!!,
                    nextId,
                    size
                )
            }
        }
        return when (safeRes) {
            is ResultWrapper.Success -> {
                val data = safeRes.data
                Triple(data.reply, data.nextId.toString(), null)
            }
            is ResultWrapper.GenericError -> {
                Triple(null, null, ErrorBody(safeRes.code, safeRes.message, safeRes.errorData))

            }
            is ResultWrapper.NetworkError -> {
                Triple(null, null, ErrorBody(ConstVariable.ERROR_NETWORK, "Network Error", null))
            }
        }

    }

    //대댓 입력[커뮤니티]
    suspend fun clickCommentReply(
        accessToken: String,
        postId: String,
        targetId: String,
        body: HashMap<String, Any>
    ): String? {
        val res = safeApiCall(Dispatchers.IO) {
            postDetailService.sendCommentReply(accessToken, postId, targetId, body)
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
    suspend fun modifyCommentReply(
        accessToken: String,
        postId: String,
        replyID: String,
        hashBody: HashMap<String, Any>
    ) :Int? {
        safeApiCall(Dispatchers.IO){postDetailService.modifyPostComment(accessToken, postId, replyID, hashBody)}.run {
            return when (this) {
                is ResultWrapper.Success -> {
                    200
                }
                is ResultWrapper.GenericError -> {
                    this.code?.toInt()
                }
                is ResultWrapper.NetworkError -> {
                    666
                }
            }
        }
    }

    //댓글 삭제[커뮤니티]
    suspend fun deleteCommentReply(
        accessToken: String,
        postId: String,
        replyId: String,
        hashBody: HashMap<String, String>
    ): String {
        val deleteRes = safeApiCall(Dispatchers.IO) {
            postDetailService.deleteCommunityPostComment(accessToken, postId, replyId, hashBody)
        }
        return when (deleteRes) {
            is ResultWrapper.Success -> {
                ConstVariable.RESULT_SUCCESS_CODE
            }
            is ResultWrapper.GenericError -> {
                deleteRes.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
            }
            is ResultWrapper.NetworkError -> {
                ConstVariable.ERROR_NETWORK
            }
        }
    }

    //댓글 좋아요 싫어요[커뮤니티]
    suspend fun callLikeDislikeComment(
        accessToken: String,
        isCancel: Boolean,
        likeType: String,
        targetId: String,
        hashBody: HashMap<String, Any>
    ) :Pair<LikeDislikeResult?,ErrorBody?> {
        if (isCancel) {
            safeApiCall(Dispatchers.IO){
                postDetailService.cancelPostLike(
                    accessToken,
                    ConstVariable.LIKE_DISLIKE_TYPE_REPLY,
                    targetId,
                    hashBody
                )
            }
        } else {
            safeApiCall(Dispatchers.IO){
                postDetailService.clickPostCommentLike(
                    accessToken,
                    likeType,
                    ConstVariable.LIKE_DISLIKE_TYPE_REPLY,
                    targetId,
                    hashBody
                )
            }
        }.run {
            return when(this){
                is ResultWrapper.Success ->{
                    Pair(this.data,null)
                }
                is ResultWrapper.GenericError->{
                    Pair(null, ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Pair(null,ErrorBody(ConstVariable.ERROR_NETWORK,"Network Error",null))
                }
            }
        }
    }

    //계정 차단[커뮤니티]
    suspend fun callBlockCommentAccount(accessToken: String, uid: String, targetUid: String, isBlock:Boolean) :String? {
         safeApiCall(Dispatchers.IO) {
            if (isBlock) {
                postDetailService.unblockUserPost(
                    accessToken,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uid
                        this[ConstVariable.KEY_TARGET_UID] = targetUid
                    }
                )
            } else {
                postDetailService.blockUserPost(
                    accessToken,
                    HashMap<String, Any>().apply {
                        this[ConstVariable.KEY_UID] = uid
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
                    this.code
                }
                is ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    //댓글 차단[커뮤니티]
    suspend fun callBlockCommentOrPost(accessToken: String, targetCommentId:String, myUid:String, targetUId: String, isBlock: Boolean) :String? {
         safeApiCall(Dispatchers.IO){if(isBlock){
            postDetailService.unblockPiecePost(
                accessToken,
                ConstVariable.LIKE_DISLIKE_TYPE_REPLY,
                targetCommentId.toString(),
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=myUid
                    this[ConstVariable.KEY_TARGET_UID]=targetUId
                }
            )
        }else{
            postDetailService.blockPiecePost(
                accessToken,
                ConstVariable.LIKE_DISLIKE_TYPE_REPLY,
                targetCommentId.toString(),
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=myUid
                    this[ConstVariable.KEY_TARGET_UID]=targetUId
                }
            )
        }}.run {
           return when(this){
                is ResultWrapper.Success->{
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError->{
                    this.code
                }
                is ResultWrapper.NetworkError->{
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    //댓글 신고[커뮤니티]
    suspend fun callReportCommunityComment(
        targetId: Int,
        uId: String,
        targetUId: String,
        reportId: Int
    ):String {
        safeApiCall(Dispatchers.IO) {
            postDetailService.reportCommunityPostComment(
                targetId,
                "REPLY",
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
    //댓글 리스트[클럽]
    suspend fun callClubComment(
        uId: String?, accessToken: String?, clubId: String, categoryCode: String, postId: Int,
        nextId: String?, size: Int,replyId:Int
    ) : Triple<List<ClubReplyData>?,ErrorBody?,String> {
        safeApiCall(Dispatchers.IO){
            if(uId == null || accessToken == null){
                postDetailService.fetchClubCommentReplys(clubId,categoryCode,postId,replyId,nextId,size)
            }else{
                postDetailService.fetchClubCommentReplys(clubId,categoryCode,postId,replyId,uId,nextId,size)
            }
        }.run {
            return when(this){
                is ResultWrapper.Success ->{
                    Triple(this.data.reply,null,this.data.nextId?:"-1")
                }
                is ResultWrapper.GenericError->{
                    Triple(null,ErrorBody(this.code,this.message,this.errorData),"-1")
                }
                is ResultWrapper.NetworkError->{
                    Triple(null,ErrorBody(ConstVariable.ERROR_NETWORK,null,null),"-1")
                }
            }
        }
    }

    //대댓 입력[클럽]
    suspend fun registerClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int,
        body: HashMap<String, Any>
    ): String? {
        val res = safeApiCall(Dispatchers.IO) {
            postDetailService.sendClubCommentReply(clubId,categoryCode, postId, replyId, body)
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

    //대댓 수정[클럽]
    suspend fun requestModifyClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int,
        reReplyId: Int,
        body: HashMap<String, Any>
    ) : String?{
        val res = safeApiCall(Dispatchers.IO) {
            if(replyId==reReplyId){
                postDetailService.modifyClubComment(clubId,categoryCode,postId,replyId,body)
            }else{
                postDetailService.modifyClubCommentReply(clubId,categoryCode, postId, replyId,reReplyId, body)
            }
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
    //댓글 신고[클럽]
    suspend fun patchReportClubComment(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int,
        uId: String,
        reportId: Int,
        parentsReplyId: Int?
    ) :String{
        safeApiCall(Dispatchers.IO){
            if(parentsReplyId ==null){
                postDetailService.reportClubComment(
                    clubId,
                    categoryCode,
                    postId,
                    replyId,
                    HashMap<String,Any>().apply {
                        this[ConstVariable.KEY_UID]=uId
                        this["reportMessageId"] =reportId
                    }
                )
            }else{
                postDetailService.reportClubCommentReply(
                    clubId,
                    categoryCode,
                    postId,
                    parentsReplyId,
                    replyId,
                    HashMap<String,Any>().apply {
                        this[ConstVariable.KEY_UID]=uId
                        this["reportMessageId"] =reportId
                    }
                )
            }
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

    //댓글 삭제[클럽]
    suspend fun deleteClubComment(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int,
        uId: String,
        resonMsg: String
    ) :String {
        safeApiCall(Dispatchers.IO){
            postDetailService.deleteClubPostComment(
                clubId,
                categoryCode,
                postId,
                replyId,
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                    this["reasonMsg"]=resonMsg
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

    //대댓글 삭제[클럽]
    suspend fun deleteClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: Int,
        replyId: Int,
        parentsReplyId: Int,
        uId: String,
        resonMsg: String
    ):String{
        safeApiCall(Dispatchers.IO){
            postDetailService.deleteClubPostCommentReply(
                clubId,
                categoryCode,
                postId,
                replyId,
                parentsReplyId,
                HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                    this["reasonMsg"]=resonMsg
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

    //댓글 차단 및 차단 해제[클럽]
    suspend fun patchClubCommentBlock(
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
    //대댓글 차단 및 차단 해제[클럽]
    suspend fun patchClubCommentReplyBlock(
        uId: String,
        clubId: String,
        categoryCode: String,
        postId: Int,
        parentsReplyId: Int,
        replyId: Int
    ): String {
        safeApiCall(Dispatchers.IO) {
            postDetailService.patchBlockClubCommentReply(
                clubId,
                categoryCode,
                postId,
                parentsReplyId,
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

    //계정 차단 [클럽]
    suspend fun patchBlockClubMember(
        clubId :String,
        memberId :Int,
        uId:String
    ) :String{
        safeApiCall(Dispatchers.IO){
            postDetailService.patchBlockClubMember(
                clubId,
                memberId,
                HashMap<String,Any>().apply {
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

}