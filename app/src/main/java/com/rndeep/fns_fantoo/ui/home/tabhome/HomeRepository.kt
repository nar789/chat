package com.rndeep.fns_fantoo.ui.home.tabhome

import android.content.Context
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.data.local.dao.HomeBannerDao
import com.rndeep.fns_fantoo.data.local.dao.MyProfileDao
import com.rndeep.fns_fantoo.data.local.dao.RecommendClubDao
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.HomeService
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.data.remote.api.UserInfoService
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.data.remote.model.PostListItemResponse
import com.rndeep.fns_fantoo.data.remote.model.userinfo.Interest
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.TempData
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    private val bannerDao: HomeBannerDao,
    @NetworkModule.ApiServer private val homeService: HomeService,
    private val recommendClubDao : RecommendClubDao,
    @NetworkModule.ApiServer private val postService: PostService,
    @NetworkModule.ApiServer private val service: UserInfoService,
    private val myProfileDao: MyProfileDao,
) :PostRepository(boardPagePostDao,postService){


    //Api를 통해 banner를 받아온 후 DB로 전송후 Banner Item return
    suspend fun getBannerItem(): List<BannerItem> {
        //TODO : Banner Item API 통신
        bannerDao.deleteAllItem()
        return if (bannerDao.insertItemList(TempData.tempBannerList).isNotEmpty()) {
            bannerDao.getAllBanner()
        } else {
            listOf<BannerItem>()
        }
    }

    //로그인 유저가 아닐시
    suspend fun getGuestData() :List<BoardPagePosts>{
        val homeListData = ArrayList<BoardPagePosts>()
        boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_HOMECATEGORY)
        homeListData.add(BoardPagePosts(type = ConstVariable.TYPE_BANNER, boardPostShowPosition = ConstVariable.DB_HOMECATEGORY))
        homeListData.add(BoardPagePosts(type = ConstVariable.TYPE_IS_NOT_LOGIN, boardPostShowPosition = ConstVariable.DB_HOMECATEGORY))
        boardPagePostDao.insertItemList(homeListData)
        return boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_HOMECATEGORY)
    }

    //유저 정보 입력했는지 확인
    suspend fun getUserInfoData(accessToken:String,uId:String){
        safeApiCall(Dispatchers.IO){service.fetchUserInfo(accessToken, IntegUid(uId))}.run {
            when(this){
                is ResultWrapper.Success->{
                    myProfileDao.insert(
                        MyProfile(
                            this.data.integUid,
                            this.data.userPhoto,
                            this.data.userNick,
                            getCodeList(this.data.interestList),
                            this.data.countryIsoTwo,
                            getGenderType(this.data.genderType.toString()),
                            this.data.birthDay,
                        )
                    )
                }
                is ResultWrapper.GenericError->{

                }
                is ResultWrapper.NetworkError->{

                }
            }
        }
    }

    private fun getGenderType(genderType: String): GenderType {
        return when (genderType) {
            "male" -> GenderType.MALE
            "female" -> GenderType.FEMALE
            else -> GenderType.UNKNOWN
        }
    }

    private fun getCodeList(list: List<Interest>) : String? {
        val codeList = mutableListOf<String>()
        list.forEach {
            codeList.add(it.code)
        }
        Timber.d("codeList: $codeList")
        return if(codeList.size == 0) {
            null
        } else {
            codeList.toString()
        }
    }

    fun getMyProfile()=myProfileDao.getMyProfile()

    suspend fun getEditProfileHomeData() : List<BoardPagePosts>{
        boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_HOMECATEGORY).run {
            boardPagePostDao.insertItemList(listOf(
                BoardPagePosts(type = ConstVariable.TYPE_BANNER, boardPostShowPosition = ConstVariable.DB_HOMECATEGORY),
                BoardPagePosts(type = ConstVariable.TYPE_IS_NOT_PROFILE, boardPostShowPosition = ConstVariable.DB_HOMECATEGORY)
            )).run {
                return boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_HOMECATEGORY)
            }
        }
    }

    //초기 값일때(page == 0) DB에 저장된 내용을 전부 지운후 새로 insert 한다.
    suspend fun callInitHomeData(page: Int,context:Context): Pair<List<BoardPagePosts>,ErrorBody?> {
        context.assets.open("mainHomeDummy.json").use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val contents = object : TypeToken<PostListItemResponse>() {}.type
                Gson().fromJson(jsonReader, contents) as PostListItemResponse
            }
        }.run {
            val homeListData = ArrayList<BoardPagePosts>()
            var errorCode :String? = null
            var errormsg :String? = null
            var errorData :ErrorData? = null
            if (page == 0) {
                boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_HOMECATEGORY)
                homeListData.add(BoardPagePosts(type = ConstVariable.TYPE_BANNER, boardPostShowPosition = ConstVariable.DB_HOMECATEGORY))
            }
            when(this.code){
                ConstVariable.RESULT_SUCCESS_CODE ->{
                    for ((index, a) in this.dataObj.post.withIndex()) {
                        if (index == this.dataObj.post.size - 2 && page == 0) {
                            homeListData.add(BoardPagePosts(type = ConstVariable.TYPE_HOME_RECOMMEND_CLUB, boardPostShowPosition = ConstVariable.DB_HOMECATEGORY))
                        }
                        when(a.type){
                            ConstVariable.TYPE_COMMUNITY->{
                                homeListData.add(BoardPagePosts(type = a.type,boardPostItem = a.data , boardPostShowPosition = ConstVariable.DB_HOMECATEGORY))
                            }
                            ConstVariable.TYPE_CLUB->{
                                homeListData.add(BoardPagePosts(type = a.type, clubPostData = a.clubData , boardPostShowPosition = ConstVariable.DB_HOMECATEGORY))
                            }
                        }
                    }
                }
                else -> {
                    errorCode=this.code
                }
            }
            boardPagePostDao.insertItemList(homeListData)
            return if(errorCode!=null){
                Pair(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_HOMECATEGORY),
                    ErrorBody(errorCode,errormsg,errorData)
                )
            }else{
                Pair(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_HOMECATEGORY), null)
            }
        }
//        when (safeRes) {
//            is ResultWrapper.Success -> {
//                if (safeRes.data.code == "200") {
//                    val tempHomeDatas = ArrayList<PostInfo>()
//                    if (page == 0) {
//                        //첫 데이터받기 전 DB 비우기
//                        postDataDao.deleteAllItemOfPageType(ConstVariable.DB_HOMECATEGORY)
//                        //첫번째 위치에 배너 type 넣고
//                        tempHomeDatas.add(
//                            PostInfo(
//                                type = ConstVariable.TYPE_BANNER,
//                                db_Type = ConstVariable.DB_HOMECATEGORY
//                            )
//                        )
//                    }
//                    for ((index, a) in safeRes.data.dataObj.list.withIndex()) {
//                        a.db_Type = ConstVariable.DB_HOMECATEGORY
//                        if (index == safeRes.data.dataObj.list.size - 2 && page == 0) {
//                            //적당한위치에 추천 클럽 끼워넣고
//                            tempHomeDatas.add(
//                                PostInfo(
//                                    type = ConstVariable.TYPE_HOME_RECOMMEND_CLUB,
//                                    db_Type = ConstVariable.DB_HOMECATEGORY
//                                )
//                            )
//                        }
//                        tempHomeDatas.add(a)
//                    }
//                    //DB에 insert
//                    if (postDataDao.insertAllItem(tempHomeDatas.toList()).isNotEmpty()) {
//                        //insert 성공 후 리스트 반환
//                        return postDataDao.getAllItemOfType(ConstVariable.DB_HOMECATEGORY)
//                    }
//                }
//            }
//            else -> return listOf()
//        }
    }

    suspend fun getAllHomeItem(): List<BoardPagePosts> {
        return boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_HOMECATEGORY)
    }

    //타입별 추천 클럽 받기
    suspend fun getRecommendClub(type: String, uid: String?): List<CommonRecommendSelect> {
        var safeRes = safeApiCall(Dispatchers.IO) { homeService.getSuitRecommendClub(uid) }
        when (safeRes) {
            is ResultWrapper.Success -> {
                recommendClubDao.deleteAllItem(type)
                val recommendClub = CommonRecommendClub()
                recommendClub.pageType = type
                recommendClub.recommendClubList = safeRes.data.clubList
                if (recommendClubDao.interItems(recommendClub).isNotEmpty()) {
                    return listOf<CommonRecommendSelect>(recommendClubDao.getRecommendClubAllItemOfType(type))
                }
                return listOf()
            }
            else -> return listOf()
        }
    }
}