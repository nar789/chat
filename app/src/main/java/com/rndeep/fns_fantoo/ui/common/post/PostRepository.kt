package com.rndeep.fns_fantoo.ui.common.post

import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.community.LikeDislikeResult
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

open class PostRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    @NetworkModule.ApiServer private val postService: PostService,
) : BaseNetRepo() {

    private suspend fun getPostItemOfType(showType: String) =
        boardPagePostDao.getAllBoardPostItemOfShowType(showType)

    //좋아요 API 통신
    suspend fun clickLikePost(
        pkID: Int,
        type: String,
        accessToken: String,
        likeType: String,
        targetId: String,
        uId: String,
        isCancel: Boolean
    ): Pair<List<BoardPagePosts>, ErrorBody?> {
        var safeRes: ResultWrapper<LikeDislikeResult>? = null
        var errorCode: Int? = null
        var errorMessage: String? = null
        var errorData: ErrorData? = null
        when (type) {
            ConstVariable.TYPE_CLUB -> {
                safeRes = safeApiCall(Dispatchers.IO) {
                    if (isCancel)
                        postService.cancelClubPostLikeDisLike(
                            accessToken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            targetId,
                            HashMap<String, Any>().apply {
                                this[ConstVariable.KEY_UID] = uId
                            }
                        )
                    else postService.callClubPostLikeDisLike(
                        accessToken,
                        likeType,
                        ConstVariable.LIKE_DISLIKE_TYPE_POST,
                        targetId,
                        HashMap<String, Any>().apply {
                            this[ConstVariable.KEY_UID] = uId
                        }
                    )
                }
            }
            ConstVariable.TYPE_COMMUNITY -> {
                safeRes = safeApiCall(Dispatchers.IO) {
                    if (isCancel)
                        postService.cancelCommunityPostLikeDisLike(
                            accessToken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            targetId,
                            HashMap<String, Any>().apply {
                                this[ConstVariable.KEY_UID] = uId
                            }
                        )
                    else postService.callCommunityPostLikeDisLike(
                        accessToken,
                        likeType,
                        ConstVariable.LIKE_DISLIKE_TYPE_POST,
                        targetId,
                        HashMap<String, Any>().apply {
                            this[ConstVariable.KEY_UID] = uId
                        }
                    )
                }
            }
        }
        val item = boardPagePostDao.getBoardPostItem(pkID)
        safeRes ?: return Pair(
            getPostItemOfType(item.boardPostShowPosition),
            ErrorBody(ConstVariable.ERROR_POST_UPDATE_FAIL, null, null)
        )
        when (safeRes) {
            is ResultWrapper.Success -> {
                changeLikeState(likeType, item.boardPostItem!!)
                item.boardPostItem!!.likeCnt = safeRes.data.like
                item.boardPostItem!!.dislikeCnt = safeRes.data.dislike
            }
            is ResultWrapper.GenericError -> {
                errorCode = safeRes.code?.toInt()
                errorMessage = safeRes.message
                errorData = safeRes.errorData
            }
            is ResultWrapper.NetworkError -> {
                errorCode = ConstVariable.ERROR_NETWORK.toInt()
            }
        }

        if (boardPagePostDao.updateItem(item) != 0 && errorCode == null) {
            return Pair(getPostItemOfType(item.boardPostShowPosition), null)
        } else {
            return Pair(
                getPostItemOfType(item.boardPostShowPosition),
                ErrorBody(errorCode.toString(), errorMessage, errorData)
            )
        }
    }

    private fun changeLikeState(likeType: String, boardItem: BoardPostData) {
        when (likeType) {
            ConstVariable.LIKE_CLICK -> {
                if (boardItem.likeYn != null) boardItem.likeYn = !boardItem.likeYn!!
                else boardItem.likeYn = true
                boardItem.dislikeYn = false
            }
            ConstVariable.DISLIKE_CLICK -> {
                if (boardItem.dislikeYn != null) boardItem.dislikeYn = !boardItem.dislikeYn!!
                else boardItem.dislikeYn = true
                boardItem.likeYn = false
            }
        }
    }

    //honor API 토신
    suspend fun clickHonorPost(
        pkID: Int,
        accesstoken: String,
        type: String,
        targetId: String,
        uId: String,
        isCancel: Boolean
    ): Pair<List<BoardPagePosts>, ErrorBody?> {
        var safeRes: ResultWrapper<BaseResponse>? = null
        when (type) {
            ConstVariable.TYPE_CLUB -> {
                safeRes = safeApiCall(Dispatchers.IO) {
                    if (isCancel) {
                        postService.cancelCommunityPostHonor(
                            accesstoken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            targetId,
                            HashMap<String, Any>().apply {
                                this[ConstVariable.KEY_UID] = uId
                            }
                        )
                    } else {
                        postService.callCommunityPostHonor(
                            accesstoken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            targetId,
                            HashMap<String, Any>().apply {
                                this[ConstVariable.KEY_UID] = uId
                            }
                        )
                    }
                }
            }
            ConstVariable.TYPE_COMMUNITY -> {
                safeRes = safeApiCall(Dispatchers.IO) {
                    if (isCancel) {
                        postService.cancelClubPostHonor(
                            accesstoken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            targetId,
                            HashMap<String, Any>().apply {
                                this[ConstVariable.KEY_UID] = uId
                            }
                        )
                    } else {
                        postService.callClubPostHonor(
                            accesstoken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            targetId,
                            HashMap<String, Any>().apply {
                                this[ConstVariable.KEY_UID] = uId
                            }
                        )
                    }
                }
            }
        }
        val item = boardPagePostDao.getBoardPostItem(pkID)
        safeRes ?: return Pair(getPostItemOfType(item.boardPostShowPosition), ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
        //값이 있을경우 pk 값이기 때문에 하나만 return 되는게 정상임
        item.boardPostItem ?: return Pair(getPostItemOfType(item.boardPostShowPosition), ErrorBody(ConstVariable.ERROR_POST_UPDATE_FAIL,null,null))
        var errorCode :Int? =null
        var errorMessage :String? =null
        var errorData :ErrorData? =null
        when(safeRes){
            is ResultWrapper.Success -> {

            }
            is ResultWrapper.GenericError -> {

            }
            is ResultWrapper.NetworkError -> {

            }
        }
        if (item.boardPostItem!!.honorYn != null) {
            item.boardPostItem?.honorYn = !item.boardPostItem?.honorYn!!
        } else {
            item.boardPostItem?.honorYn = true
        }
        item.boardPostItem?.honorCnt=20
        if (boardPagePostDao.updateItem(item) != 0 && errorCode==null) {
            return Pair(getPostItemOfType(item.boardPostShowPosition),null)
        } else {
            return Pair(getPostItemOfType(item.boardPostShowPosition),ErrorBody(errorCode.toString(),errorMessage,errorData))
        }
    }

    //게시글 지우기
    suspend fun bolckPiecePost(pkID: Int, accessToken:String, uId:String): Pair<List<BoardPagePosts>,ErrorBody?> {
        val item = boardPagePostDao.getBoardPostItem(pkID)
        val postItem = boardPagePostDao.getBoardPostItem(pkID).boardPostItem ?:
            return Pair(getPostItemOfType(item.boardPostShowPosition), ErrorBody(ConstVariable.ERROR_POST_UPDATE_FAIL, null, null))

        var safeRes :ResultWrapper<Unit>? =null
        when(item.type){
            ConstVariable.TYPE_COMMUNITY->{
                safeRes=safeApiCall(Dispatchers.IO) {
                    if (postItem.pieceBlockYn==true) {
                        postService.unblockClubPiece(
                            accessToken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            postItem.postId.toString(),
                            HashMap<String,Any>().apply {
                                this[ConstVariable.KEY_UID]=uId
                                this[ConstVariable.KEY_TARGET_UID]=postItem.integUid
                            }
                        )
                    } else {
                        postService.blockClubPiece(
                            accessToken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            postItem.postId.toString(),
                            HashMap<String,Any>().apply {
                                this[ConstVariable.KEY_UID]=uId
                                this[ConstVariable.KEY_TARGET_UID]=postItem.integUid
                            }
                        )
                    }
                }
            }
            ConstVariable.TYPE_CLUB->{
                safeRes=safeApiCall(Dispatchers.IO) {
                    if (postItem.pieceBlockYn==true) {
                        postService.unblockCommunityPiece(
                            accessToken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            postItem.postId.toString(),
                            HashMap<String,Any>().apply {
                                this[ConstVariable.KEY_UID]=uId
                                this[ConstVariable.KEY_TARGET_UID]=postItem.integUid
                            }
                        )
                    } else {
                        postService.blockCommunityPiece(
                            accessToken,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST,
                            postItem.postId.toString(),
                            HashMap<String,Any>().apply {
                                this[ConstVariable.KEY_UID]=uId
                                this[ConstVariable.KEY_TARGET_UID]=postItem.integUid
                            }
                        )
                    }
                }
            }
        }
        safeRes ?: return Pair(
            getPostItemOfType(item.boardPostShowPosition),
            ErrorBody(ConstVariable.ERROR_POST_UPDATE_FAIL, null, null)
        )
        var errorCode :Int? =null
        var errorMessage :String? =null
        var errorData :ErrorData? =null
        when(safeRes){
            is ResultWrapper.Success->{
                if(item.boardPostItem!!.pieceBlockYn==null)item.boardPostItem!!.pieceBlockYn=true
                else item.boardPostItem!!.pieceBlockYn=!item.boardPostItem!!.pieceBlockYn!!
            }
            is ResultWrapper.GenericError -> {
                errorCode=safeRes.code?.toInt()
                errorMessage=safeRes.message
                errorData=safeRes.errorData
            }
            is ResultWrapper.NetworkError -> {
                errorCode=ConstVariable.ERROR_NETWORK.toInt()
            }
        }
        return if(boardPagePostDao.updateItem(item)!=0 && errorCode==null){
            Pair(boardPagePostDao.getAllBoardPostItemOfShowType(item.boardPostShowPosition),null)
        }else{
            Pair(boardPagePostDao.getAllBoardPostItemOfShowType(item.boardPostShowPosition),ErrorBody(errorCode.toString(),errorMessage,errorData))
        }
    }

    //아마 작성자 auth UID 가 들어 갈 것으로 생각
    suspend fun blockUserPostFromDB(pkId: Int,accessToken:String,uId:String): Pair<List<BoardPagePosts>,ErrorBody?> {
        val item = boardPagePostDao.getBoardPostItem(pkId)
        val postItem = item.boardPostItem?:
            return Pair(boardPagePostDao.getAllBoardPostItemOfShowType(item.boardPostShowPosition),
                ErrorBody(ConstVariable.ERROR_POST_UPDATE_FAIL,null,null)
            )

        var safeRes : ResultWrapper<Unit>? =null
        when(item.type){
            ConstVariable.TYPE_CLUB -> {
                safeRes=safeApiCall(Dispatchers.IO){if(postItem.userBlockYn==true){
                    postService.unblockAccountClubPost(
                        accessToken,
                        HashMap<String,Any>().apply {
                            this[ConstVariable.KEY_UID]=uId
                            this[ConstVariable.KEY_TARGET_UID]=postItem.integUid
                        }
                    )
                }else{
                    postService.blockAccountClubPost(
                        accessToken,
                        HashMap<String,Any>().apply {
                            this[ConstVariable.KEY_UID]=uId
                            this[ConstVariable.KEY_TARGET_UID]=postItem.integUid
                        }
                    )}
                }
            }
            ConstVariable.TYPE_COMMUNITY -> {
                safeRes=safeApiCall(Dispatchers.IO){if(postItem.userBlockYn==true) {
                    postService.unblockAccountCommunityPost(
                        accessToken,
                        HashMap<String, Any>().apply {
                            this[ConstVariable.KEY_UID] = uId
                            this[ConstVariable.KEY_TARGET_UID] = postItem.integUid
                        }
                    )
                }else{
                    postService.blockAccountCommunityPost(
                        accessToken,
                        HashMap<String, Any>().apply {
                            this[ConstVariable.KEY_UID] = uId
                            this[ConstVariable.KEY_TARGET_UID] = postItem.integUid
                        }
                    )
                }}
            }
        }
        safeRes?: return Pair(boardPagePostDao.getAllBoardPostItemOfShowType(item.boardPostShowPosition),
            ErrorBody(ConstVariable.ERROR_POST_UPDATE_FAIL,null,null)
        )
        var errorCode : Int? =null
        var errorMsg : String? =null
        var errorData : ErrorData? =null
        when(safeRes){
            is ResultWrapper.Success ->{
                var authId = ""
                when (item.type) {
                    ConstVariable.TYPE_CLUB -> {
                        authId = postItem.integUid
                    }
                    ConstVariable.TYPE_COMMUNITY -> {
                        authId = postItem.integUid
                    }
                }
                val authItems = boardPagePostDao.getPostItemWithAuthID(authId)

                for (a in authItems) {
                    when (a.type) {
                        ConstVariable.TYPE_CLUB -> {
                            a.boardPostItem!!.userBlockYn =
                                if (a.boardPostItem!!.userBlockYn == null) true else !a.boardPostItem!!.userBlockYn!!
                        }
                        ConstVariable.TYPE_COMMUNITY -> {
                            a.boardPostItem!!.userBlockYn =
                                if (a.boardPostItem!!.userBlockYn == null) true else !a.boardPostItem!!.userBlockYn!!
                        }
                    }
                }
                boardPagePostDao.updateItemList(authItems)
            }
            is ResultWrapper.GenericError ->{
                errorCode=safeRes.code?.toInt()
                errorMsg=safeRes.message
                errorData=safeRes.errorData
            }
            is ResultWrapper.NetworkError->{
                errorCode=ConstVariable.ERROR_NETWORK.toInt()
            }
        }
        return if(errorCode==null){
            Pair(boardPagePostDao.getAllBoardPostItemOfShowType(item.boardPostShowPosition),null)
        }else{
            Pair(boardPagePostDao.getAllBoardPostItemOfShowType(item.boardPostShowPosition),ErrorBody(errorCode.toString(),errorMsg,errorData))
        }

    }

    //저장하기
    suspend fun savePost(accessToken:String,uId:String,postId : Int) : Boolean{
        val postData = boardPagePostDao.getBoardPostItem(postId)
        postData.boardPostItem?: return false
        safeApiCall(Dispatchers.IO){
            if(postData.type==ConstVariable.TYPE_COMMUNITY){
                postService.callBookmarkPost(accessToken ,postData.boardPostItem!!.postId.toString(),HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                })
            }else{
                postService.callBookmarkPost(accessToken ,postData.boardPostItem!!.postId.toString(),HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                })
            }
        }.run {
            when(this){
                is ResultWrapper.Success->{
                   return true
                }
                is ResultWrapper.GenericError->{
                   return false
                }
                is ResultWrapper.NetworkError->{
                   return false
                }
            }
        }
    }
}