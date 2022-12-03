package com.rndeep.fns_fantoo.ui.community.mypage

import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.data.remote.model.community.MyCommentItem
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CommunityMyRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    @NetworkModule.ApiServer private val postService: PostService
) : PostRepository(boardPagePostDao, postService) {

    suspend fun getCommunityAlarmState(uId :String):Pair<Boolean?,ErrorBody?>{
        safeApiCall(Dispatchers.IO){
            postService.getMyCommunityAlimState(uId)
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data.comAgree,null)
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

    suspend fun changeCommunityAlarmState(uId:String):Pair<Boolean?,ErrorBody?>{
        safeApiCall(Dispatchers.IO){postService.patchMyCommunityAlimState(
            HashMap<String, Any>().apply {
                this["alimType"]="COMMUNITY"
                this[ConstVariable.KEY_UID]=uId
            }
        )}.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data.comAgree,null)
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

    suspend fun getMyCreatePostItem(accessToken: String,uId: String,nextId: Int,size: String,type:String): Triple<List<BoardPagePosts>?,Int, ErrorBody?> {
        safeApiCall(Dispatchers.IO){postService.getMyPostList(accessToken,uId,nextId,size)}.run {
            var errorCode :Int? =null
            var errorMessage :String? =null
            var errorData :ErrorData? =null
            var receiveNextId =0
            if(type=="init") boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_COMMUNITY_MY_POST)
            when(this){
                is ResultWrapper.Success->{
                    val dbItem = this.data.bookmark.map {
                        BoardPagePosts(type = ConstVariable.TYPE_COMMUNITY, boardPostItem = it, boardPostShowPosition = ConstVariable.DB_COMMUNITY_MY_POST)
                    }
                    boardPagePostDao.insertItemList(dbItem)
                    receiveNextId=this.data.nextId
                }
                is ResultWrapper.GenericError->{
                    errorCode=this.code?.toInt()
                    errorMessage=this.message
                    errorData=this.errorData
                    receiveNextId=-1
                }
                is ResultWrapper.NetworkError->{
                    errorCode=ConstVariable.ERROR_NETWORK.toInt()
                    receiveNextId=-1
                }
            }

            return if(errorCode==null){
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_COMMUNITY_MY_POST),receiveNextId,null)
            }else{
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_COMMUNITY_MY_POST),receiveNextId,ErrorBody(errorCode.toString(),errorMessage,errorData))
            }
        }
    }

    suspend fun getMyBookmarkPostList(
        accessToken: String,
        uId: String,
        nextId: Int,
        size: String,
        type:String
    ): Triple<List<BoardPagePosts>,Int, ErrorBody?> {
        safeApiCall(Dispatchers.IO) {
            postService.getBookmarkPostList(accessToken, uId, nextId, size)
        }.run {
            var errorCode :Int? =null
            var errorMessage :String? =null
            var errorData :ErrorData? =null
            if(type == "init") boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_COMMUNITY_BOOKMARK_POST)
            var receiveNextId=0
            when(this){
                is ResultWrapper.Success -> {
                    val dbItem = this.data.bookmark.map {
                        BoardPagePosts(type = ConstVariable.TYPE_COMMUNITY, boardPostItem = it , boardPostShowPosition = ConstVariable.DB_COMMUNITY_BOOKMARK_POST)
                    }
                    boardPagePostDao.insertItemList(dbItem)
                    receiveNextId=this.data.nextId
                }
                is ResultWrapper.GenericError -> {
                    errorCode=this.code?.toInt()
                    errorMessage=this.message
                    errorData=this.errorData
                    receiveNextId=-1
                }
                is ResultWrapper.NetworkError->{
                    errorCode=ConstVariable.ERROR_NETWORK.toInt()
                    receiveNextId=-1
                }
            }
            return if(errorCode == null){
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_COMMUNITY_BOOKMARK_POST),receiveNextId, null)
            }else{
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_COMMUNITY_BOOKMARK_POST),receiveNextId, ErrorBody(errorCode.toString(),errorMessage,errorData))
            }
        }

    }

    suspend fun getMyCreateCommentItem(
        accessToken: String,
        uId: String,
        nextId: Int,
        size: String,
    ): Triple<List<MyCommentItem>, Int, ErrorBody?> {
        safeApiCall(Dispatchers.IO){postService.getMyReply(accessToken,uId,nextId,size)}.run {
            var errorCode :Int? =null
            var errorMessage :String? =null
            var errorData :ErrorData? =null
            var receiveNextId=0
            var dbItem = listOf<MyCommentItem>()
            when(this){
                is ResultWrapper.Success -> {
                    dbItem = this.data.reply
                    receiveNextId=this.data.nextId
                }
                is ResultWrapper.GenericError -> {
                    errorCode=this.code?.toInt()
                    errorMessage=this.message
                    errorData=this.errorData
                }
                is ResultWrapper.NetworkError->{
                    errorCode=ConstVariable.ERROR_NETWORK.toInt()
                }
            }
            return if(errorCode == null){
                Triple(dbItem,receiveNextId, null)
            }else{
                Triple(dbItem,receiveNextId, ErrorBody(errorCode.toString(),errorMessage,errorData))
            }
        }
    }

}