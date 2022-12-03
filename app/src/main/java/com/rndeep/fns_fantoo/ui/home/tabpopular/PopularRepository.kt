package com.rndeep.fns_fantoo.ui.home.tabpopular

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.rndeep.fns_fantoo.data.local.dao.*
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.HomeService
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.data.remote.model.PostListItemResponse
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.data.remote.model.RecommendClubData
import com.rndeep.fns_fantoo.ui.common.TempData
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PopularRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    private val popularTrendTagDao: PopularTrendTagDao,
    private val curationDao: CurationDao,
    @NetworkModule.ApiServer private val homeService: HomeService,
    private val recommendClubDao: RecommendClubDao,
    @NetworkModule.ApiServer private val postService: PostService
) :PostRepository(boardPagePostDao,postService){

    suspend fun getTrendItem(): List<TrendTagItem> {
        //TODO : Trend Item API 통신
        val tempBannerArray = ArrayList<TrendTagItem>()
        for (a in TempData.tempStrings) {
            tempBannerArray.add(a)
        }
        //------------------------
        popularTrendTagDao.deleteAllTagItem()
        return if (popularTrendTagDao.insertTagItemList(tempBannerArray).isNotEmpty()) {
            popularTrendTagDao.getAllTrendTag()
        } else {
            listOf()
        }
    }

    suspend fun getCurationData(): List<CurationDataItem> {
        curationDao.deleteAllItem()
        return if (curationDao.insertItemList(TempData.tempCurationList).isNotEmpty()) {
            curationDao.getAllCuration()
        } else {
            listOf()
        }
    }


    suspend fun getPopularData(page: Int,context :Context): Pair<List<BoardPagePosts>,ErrorBody?> {
        val res =context.assets.open("mainHomeDummy.json").use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val contents = object : TypeToken<PostListItemResponse>() {}.type
                Gson().fromJson(jsonReader, contents) as PostListItemResponse
            }
        }
        val popularDatas = ArrayList<BoardPagePosts>()
        if (page == 0) {
            boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_POPULARCATEGORY)
            popularDatas.add(BoardPagePosts(type = ConstVariable.TYPE_BANNER, boardPostShowPosition = ConstVariable.DB_POPULARCATEGORY))
        }
        when(res.code){
            ConstVariable.RESULT_SUCCESS_CODE ->{

                for ((index, a) in res.dataObj.post.withIndex()) {
                    if (index == 1 && page == 0) {
                        popularDatas.add(BoardPagePosts(type = ConstVariable.TYPE_POPULAR_CURATION, boardPostShowPosition = ConstVariable.DB_POPULARCATEGORY))
                    }
                    if (index == res.dataObj.post.size - 2 && page == 0) {
                        popularDatas.add(BoardPagePosts(type = ConstVariable.TYPE_HOME_RECOMMEND_CLUB, boardPostShowPosition = ConstVariable.DB_POPULARCATEGORY))
                    }
                    when(a.type){
                        ConstVariable.TYPE_COMMUNITY->{
                            popularDatas.add(BoardPagePosts(type = a.type,boardPostItem = a.data , boardPostShowPosition = ConstVariable.DB_POPULARCATEGORY))
                        }
                        ConstVariable.TYPE_CLUB->{
                            popularDatas.add(BoardPagePosts(type = a.type, clubPostData = a.clubData , boardPostShowPosition = ConstVariable.DB_POPULARCATEGORY))
                        }
                    }
                }


            }
            else -> {
            }
        }
        boardPagePostDao.insertItemList(popularDatas)
        return Pair(boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_POPULARCATEGORY),null)
//        when (safeRes) {
//            is ResultWrapper.Success -> {
//                if (safeRes.data.code == "200") {
//                    //popular 데이터
//                    val tempHomeDatas = ArrayList<PostInfo>()
//                    if (page == 0) {
//                        postDataDao.deleteAllItemOfPageType(ConstVariable.DB_POPULARCATEGORY)
//                        tempHomeDatas.add(
//                            PostInfo(
//                                type = ConstVariable.TYPE_BANNER,
//                                db_Type = ConstVariable.DB_POPULARCATEGORY
//                            )
//                        )
//                    }
//
//                    for ((index, a) in safeRes.data.dataObj.list.withIndex()) {
//                        a.db_Type = ConstVariable.DB_POPULARCATEGORY
//                        if (index == 1 && page == 0) tempHomeDatas.add(
//                            PostInfo(
//                                type = ConstVariable.TYPE_POPULAR_CURATION,
//                                db_Type = ConstVariable.DB_POPULARCATEGORY
//                            )
//                        )
//                        if (index == safeRes.data.dataObj.list.size - 2 && page == 0) tempHomeDatas.add(
//                            PostInfo(
//                                type = ConstVariable.TYPE_HOME_RECOMMEND_CLUB,
//                                db_Type = ConstVariable.DB_POPULARCATEGORY
//                            )
//                        )
//                        tempHomeDatas.add(a)
//                    }
//                    if (postDataDao.insertAllItem(tempHomeDatas).isNotEmpty()) {
//                        return postDataDao.getAllItemOfType(ConstVariable.DB_POPULARCATEGORY)
//                    }
//                }
//            }
//            else -> return listOf()
//        }
//        return listOf()
    }

    suspend fun getAllPopularItem(): List<BoardPagePosts> {
        return boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_POPULARCATEGORY)
//        return postDataDao.getAllItemOfType(ConstVariable.DB_POPULARCATEGORY)
    }

    //타입별 추천 클럽 받기
    suspend fun getRecommendClub(type: String): List<CommonRecommendSelect> {
//        var safeRes: ResultWrapper<RecommendClubData>? = null
//        when (type) {
//            ConstVariable.DB_HOMECATEGORY -> {
//                safeRes = safeApiCall(Dispatchers.IO) { homeService.getSuitRecommendClub() }
//            }
//            ConstVariable.DB_POPULARCATEGORY -> {
//                safeRes = safeApiCall(Dispatchers.IO) { homeService.getHotRecommendClub() }
//            }
//        }
//        safeRes ?: return listOf()
//        when (safeRes) {
//            is ResultWrapper.Success -> {
//                if (safeRes.data.code == "200") {
//                    recommendClubDao.deleteAllItem(type)
//                    val recommendClub = CommonRecommendClub()
//                    recommendClub.pageType = type
//                    recommendClub.recommendClubList = safeRes.data.recommendList
//                    if (recommendClubDao.interItems(recommendClub).isNotEmpty()) {
//                        return recommendClubDao.getRecommendClubAllItemOfType(type)
//                    }
//                }
//            }
//            else -> return listOf()
//
//        }
        val recommendClub= CommonRecommendClub()
        recommendClub.pageType=type
        recommendClub.recommendClubList= TempData.tempRecommendClubList
        return listOf<CommonRecommendSelect>(CommonRecommendSelect("","",recommendClub.recommendClubList,recommendClub.pageType))
    }

}