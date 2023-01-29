package com.rndeep.fns_fantoo.ui.club.detail

import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.data.remote.model.*
import com.rndeep.fns_fantoo.data.remote.model.club.ClubLoginResult
import com.rndeep.fns_fantoo.data.remote.model.club.ClubNoticeItem
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubDelegatingInfoDto
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject

class ClubPageRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    @NetworkModule.ApiServer private val postService: PostService,
    @NetworkModule.ApiServer private val clubService: ClubService
) : PostRepository(boardPagePostDao, postService) {

    suspend fun getClubBasicInfo(clubId: String, uid: String?):Triple<ClubBasicInfo?,ErrorBody?,ClubDelegatingInfoDto?> {
        var errorCode : String? =null
        var errorMsg : String? =null
        var errorData : ErrorData? =null

        var favoriteResult =false
        if (uid!=null){
            favoriteResult=checkClubFavorite(clubId,uid)
        }

        var openYNResult =false
        var isMemberResult=false
        var memberIdResult :Int? =null
        var clubJoinState :Int = 0
        var clubDelegateItem :ClubDelegatingInfoDto? =null

        val checkResult =clubLoginOfMember(clubId,uid)
        checkResult.first?.let {
            isMemberResult = (it.memberId !=-1)
            memberIdResult = it.memberId
            clubJoinState = it.joinStatus
            if(it.memberId !=-1 && it.delegateStatus==1 && uid != null){
                clubDelegateItem=getClubDelegateMyInfo(clubId,uid)
            }
        }

        var memberLevelResult = -1
        if(memberIdResult !=null && memberIdResult != -1 && uid !=null){
            memberLevelResult=getClubMemberLevel(clubId,uid,memberIdResult!!)
        }
        var clubBasicInfo : ClubBasicInfo? =null
        safeApiCall(Dispatchers.IO) { clubService.getClubBasicInfo(clubId, uid ?: "guest") }.run {
           when (this) {
                is ResultWrapper.Success -> {
                    clubBasicInfo=this.data.apply {
                        favoriteYN=favoriteResult
                        isMember=isMemberResult
                        memberId=memberIdResult
                        memberLevel=memberLevelResult
                        this.clubJoinState=clubJoinState
                    }
                }
                is ResultWrapper.GenericError -> {
                    errorCode = this.code
                    errorMsg = this.message
                    errorData = this.errorData
                }
                is ResultWrapper.NetworkError -> {
                    errorCode= ConstVariable.ERROR_NETWORK
                }
            }
        }
        return if(errorCode !=null){
            Triple(null,ErrorBody(errorCode,errorMsg,errorData),null)
        }else{
            Triple(clubBasicInfo,null,clubDelegateItem)
        }

    }

    suspend fun checkClubFavorite(clubId:String, uid:String):Boolean{
        safeApiCall(Dispatchers.IO){clubService.getClubFavoriteInfo(clubId, uid)}.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.favoriteYn
                }
                else ->{
                    false
                }
            }
        }
    }

    suspend fun getClubMemberLevel(clubId:String,uId: String,memberId :Int) : Int{
        safeApiCall(Dispatchers.IO){clubService.getClubMemberInfo(clubId,memberId,uId)}.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.memberLevel
                }
                else ->{
                    -1
                }
            }
        }
    }

    suspend fun clubLoginOfMember(clubId: String, uId: String?):Pair<ClubLoginResult?,String?>{
        safeApiCall(Dispatchers.IO){clubService.loginClub(clubId,HashMap<String,Any>().apply {
            this[ConstVariable.KEY_UID]=uId?:"null"
        })}.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data,null)
                }
                is ResultWrapper.GenericError ->{
                    if(this.code=="FE3001"){
                        //비공개클럽 비로그인
                        Pair(null,null)
                    }else{
                        //그냥 에러
                        Pair(null,this.code)
                    }
                }
                is ResultWrapper.NetworkError->{
                    //그냥 에러
                    Pair(null,ConstVariable.ERROR_NETWORK)
                }
            }
        }
    }

    suspend fun patchClubFavorite(clubId: String,uId: String) : Boolean{
        safeApiCall(Dispatchers.IO){clubService.patchClubFavoriteInfo(clubId,HashMap<String,Any>().apply {
            this[ConstVariable.KEY_UID]=uId
        })}.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.favoriteYn
                }
                else ->{
                    false
                }
            }
        }
    }

    suspend fun getClubDelegateMyInfo(clubId:String,uId:String) : ClubDelegatingInfoDto?{
        safeApiCall(Dispatchers.IO){clubService.getClubDelegateOfMe(clubId,uId)}.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data
                }
                is ResultWrapper.GenericError->{
                    null
                }
                is ResultWrapper.NetworkError->{
                    null
                }
            }
        }
    }

    suspend fun patchClubMasterDelegateState(clubId:String,uId:String,isAccept :Boolean):String{
        safeApiCall(Dispatchers.IO){
            if(isAccept){
                clubService.acceptClubMasterDelegate(clubId,HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                })
            }else{
                clubService.rejectClubMasterDelegate(clubId,HashMap<String,Any>().apply {
                    this[ConstVariable.KEY_UID]=uId
                })
            }
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    ConstVariable.RESULT_SUCCESS_CODE
                }
                is ResultWrapper.GenericError ->{
                    this.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                is ResultWrapper.NetworkError -> {
                    ConstVariable.ERROR_NETWORK
                }
            }
        }
    }

    suspend fun getClubCategory(clubID:String,uId:String) : List<ClubCategoryItem>{
        safeApiCall(Dispatchers.IO){clubService.getClubCategoryList(clubID,uId,"club")}.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.categoryList
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

    suspend fun getClubHomeNoticeData(clubId:String,uId:String?,nextId:Int?): Triple<List<ClubNoticeItem>,ErrorBody?,Int> {
        safeApiCall(Dispatchers.IO){
            if(uId!=null){
                clubService.fetchClubNotice(clubId,uId,nextId,2)
            }else{
                clubService.fetchClubNoticeForGuest(clubId,nextId,2)
            }
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    val result = this.data
                    Triple(result.postList,null,result.nextId)
                }
                is ResultWrapper.GenericError->{
                    Triple(listOf(),ErrorBody(this.code,this.message,this.errorData),-1)
                }
                is ResultWrapper.NetworkError->{
                    Triple(listOf(),ErrorBody(ConstVariable.ERROR_NETWORK,null,null),-1)
                }
            }
        }
    }

    //홈 글 리스트
    suspend fun getDetailHomePostData(
        db_type: String,
        clubId: String,
        categoryCode: String,
        isLogin: Boolean,
        uId :String?,
        nextId :String?,
        size :String,
        sort :String,
        type : String
    ): Triple<List<BoardPagePosts>,ErrorBody?,Int> {
        safeApiCall(Dispatchers.IO) {
            if(!isLogin){
                clubService.getClubCategoryPostForGuest(
                    categoryCode,
                    clubId,
                    nextId,
                    size,
                    sort
                )
            }else{
                clubService.getClubCategoryPost(
                    categoryCode,
                    clubId,
                    uId!!,
                    nextId,
                    size,
                    sort
                )
            }
        }.run {
            val tempItem = arrayListOf<BoardPagePosts>()
            if(type=="Init"){
                boardPagePostDao.deleteAllItemOfShowType(db_type)
                tempItem.add(
                    BoardPagePosts(
                        type = ConstVariable.TYPE_COMMUNITY_NOTICE,
                        boardPostShowPosition = db_type
                    )
                )
            }
            var errorCode : String?=null
            var errorMessage : String?=null
            var errorData : ErrorData?=null
            var resultNextId = 0
            when(this){
                is ResultWrapper.Success->{
                    for (a in this.data.postList) {
                        tempItem.add(BoardPagePosts(type=ConstVariable.TYPE_CLUB, clubPostData = a, boardPostShowPosition = db_type))
                    }
                    resultNextId=this.data.nextId?:-1
                }
                is ResultWrapper.GenericError->{
                    errorCode=this.code
                    errorMessage=this.message
                    errorData=this.errorData
                    resultNextId=-1
                }
                is ResultWrapper.NetworkError->{
                    errorCode=ConstVariable.ERROR_NETWORK
                    resultNextId=-1
                }
            }
            boardPagePostDao.insertItemList(tempItem)
            return if(errorCode!=null){
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(db_type),
                    ErrorBody(errorCode,errorMessage,errorData),
                    resultNextId
                )
            }else{
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(db_type),null,resultNextId)
            }
        }

    }


    suspend fun fetchDetailFreeBoardCategory(
        clubId: String,
        categoryCode: String,
        integUid: String
    ) :Pair<List<ClubSubCategoryItem>?,ErrorBody?>{
        safeApiCall(Dispatchers.IO){clubService.fetchClubSubCategory(
            clubId,categoryCode,integUid
        )}.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data.categoryList,null)
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
    //자유게시판 글 리스트
    suspend fun getDetailFreeBoardPostData(
        path: String,
        uId: String,
        nextId: String?,
        size : Int
    ): Triple<List<BoardPagePosts>, ErrorBody?, Int> {
        boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_CLUB_DETAIL_FREEBOARD)
        safeApiCall(Dispatchers.IO){clubService.fetchFreeBoardPost(
            path,
            uId,
            nextId,
            size,
            "sort"
        )}.run {
            var errorCode : String? =null
            var errorMsg : String? =null
            var errorData : ErrorData? =null
            var resultNextId = -1
            when(this){
                is ResultWrapper.Success->{
                    val tempItem = arrayListOf<BoardPagePosts>()
                    for (a in this.data.postList) {
                        tempItem.add(BoardPagePosts(type=ConstVariable.TYPE_CLUB, clubPostData = a, boardPostShowPosition = ConstVariable.DB_CLUB_DETAIL_FREEBOARD))
                    }
                    boardPagePostDao.insertItemList(tempItem).isNotEmpty()
                    resultNextId=this.data.nextId?:-1
                }
                is ResultWrapper.GenericError -> {
                    errorCode = this.code
                    errorMsg = this.message
                    errorData = this.errorData
                }
                is ResultWrapper.NetworkError->{
                    errorCode = ConstVariable.ERROR_NETWORK
                }
            }
            return if(errorCode!=null){
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_CLUB_DETAIL_FREEBOARD),
                    ErrorBody(errorCode,errorMsg,errorData),resultNextId
                )
            }else{
                Triple(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_CLUB_DETAIL_FREEBOARD),null,resultNextId)
            }
        }

    }

    //아카이브 image type 게시판 dummy
    suspend fun getArchiveImageList(
        postUrl: String,
        uId: String,
        nextId: String?,
        size: Int,
        sort: String?
    ): Triple<List<ClubPostData>?,Int,ErrorBody?> {
        safeApiCall(Dispatchers.IO) {
            clubService.fetchFreeBoardPost(
                postUrl,
                uId,
                nextId,
                size,
                sort
            )
        }.run {
            return when(this){
                is ResultWrapper.Success -> {
                    Triple(this.data.postList, this.data.nextId ?: -1, null)
                }
                is ResultWrapper.GenericError -> {
                    Triple(null, -1, ErrorBody(this.code, this.message, this.errorData))
                }
                is ResultWrapper.NetworkError -> {
                    Triple(null, -1, ErrorBody(ConstVariable.ERROR_NETWORK, null, null))
                }
            }
        }
    }

    suspend fun getArchiveCommonData(
        postUrl: String,
        uId: String,
        nextId: String?,
        size: Int,
        sort: String?
    ): Triple<List<BoardPagePosts>,Int,ErrorBody?> {
        safeApiCall(Dispatchers.IO) {
            clubService.fetchFreeBoardPost(
                postUrl,
                uId,
                nextId,
                size,
                sort
            )
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_CLUB_DETAIL_ARCHIVE_COMMON)
                    val tempItem = arrayListOf<BoardPagePosts>()
                    tempItem.add(BoardPagePosts(type="title", boardPostShowPosition = ConstVariable.DB_CLUB_DETAIL_ARCHIVE_COMMON))
                    for (a in this.data.postList) {
                        tempItem.add(BoardPagePosts(type=ConstVariable.TYPE_CLUB, clubPostData = a, boardPostShowPosition = ConstVariable.DB_CLUB_DETAIL_ARCHIVE_COMMON))
                    }
                    boardPagePostDao.insertItemList(tempItem)
                    Triple(
                        boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_CLUB_DETAIL_ARCHIVE_COMMON),
                        this.data.nextId?:-1,
                        null
                    )
                }
                is ResultWrapper.GenericError->{
                    Triple(listOf<BoardPagePosts>(),-1,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Triple(listOf<BoardPagePosts>(),-1,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }


    //    ---------금고---------------
    suspend fun callOfMoneyBoxInfo(): MoneyBoxTopInfo {
        return MoneyBoxTopInfo("아이유", null, 12145)
    }

    suspend fun callOfMoneyBoxRank(): List<MoneyBoxRanking> {

        return listOf(
            MoneyBoxRanking(1, "1등은나", 2330),
            MoneyBoxRanking(2, "아이유팬", 1100),
            MoneyBoxRanking(3, "BTSARMY", 800),
            MoneyBoxRanking(4, "FANTOO", 594),
            MoneyBoxRanking(5, "도원", 232),
        )
    }

    suspend fun callOfMoneyBoxWithdraw(): MoneyBoxWithDrawItem {
        return MoneyBoxWithDrawItem(
            6,
            listOf(
                MoneyBoxWithDrawListItem("sunny", 100, 1654593968455, 11023),
                MoneyBoxWithDrawListItem("sunny1", 10, 1654553968455, 11023),
                MoneyBoxWithDrawListItem("sunny2", 530, 1653593968455, 11023),
                MoneyBoxWithDrawListItem("sunny3", 170, 1652593968455, 11023),
                MoneyBoxWithDrawListItem("sunny4", 18, 1654193968455, 11023),
                MoneyBoxWithDrawListItem("sunny5", 15, 1654233968455, 11023),
                MoneyBoxWithDrawListItem("sunny6", 80, 1654003968455, 11023),
                MoneyBoxWithDrawListItem("sunny7", 13, 1653253968455, 11023),
                MoneyBoxWithDrawListItem("sunny8", 89, 1652113968455, 11023),
                MoneyBoxWithDrawListItem("sunny9", 1, 1650003968455, 11023),
                MoneyBoxWithDrawListItem("sunny10", 22, 1642353968455, 11023),
                MoneyBoxWithDrawListItem("sunny11", 60606, 1640213968455, 11023),
                MoneyBoxWithDrawListItem("sunny12", 445, 1638413968455, 11023),
                MoneyBoxWithDrawListItem("sunny13", 23, 1621543968455, 11023),
                MoneyBoxWithDrawListItem("sunny14", 77, 1610213968455, 11023),
                MoneyBoxWithDrawListItem("sunny15", 134, 160000968455, 11023),
                MoneyBoxWithDrawListItem("sunny16", 12, 1554593968455, 11023),
            )
        )
    }

    suspend fun getSearchResult(clubId :String,keyWord : String,nextId :Int?,size :Int): Triple<List<ClubPostData>,Int,ErrorBody?> {
        safeApiCall(Dispatchers.IO){clubService.searchClubPost(clubId,keyWord,nextId,size)}.run {
            return when(this){
                is ResultWrapper.Success->{
                    Triple(this.data.postList,this.data.nextId?:-1,null)
                }
                is ResultWrapper.GenericError->{
                    Triple(listOf(),-1,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Triple(listOf(),-1,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }
}