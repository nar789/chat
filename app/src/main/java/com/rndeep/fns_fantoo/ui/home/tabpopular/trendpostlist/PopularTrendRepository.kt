package com.rndeep.fns_fantoo.ui.home.tabpopular.trendpostlist

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.api.HomeService
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.data.remote.model.PostListItemResponse
import com.rndeep.fns_fantoo.utils.ConstVariable
import javax.inject.Inject

class PopularTrendRepository @Inject constructor(
    @NetworkModule.ApiServer private val homeService: HomeService,
    private val boardPagePostDao: BoardPagePostDao,
    @NetworkModule.ApiServer private val postService: PostService
) : PostRepository(boardPagePostDao,postService) {

    suspend fun callTrendPostData(page: Int,context:Context): List<BoardPagePosts> {
        context.assets.open("mainHomeDummy.json").use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val contents = object : TypeToken<PostListItemResponse>() {}.type
                Gson().fromJson(jsonReader, contents) as PostListItemResponse
            }
        }.run {
            when (this.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    val tempHomeDatas = ArrayList<BoardPagePosts>()
                    if (page == 0) {
                        //첫 데이터받기 전 DB 비우기
                        boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_TRENDPOST)
                    }
                    for ((index, a) in this.dataObj.post.withIndex()) {
                        when(a.type){
                            ConstVariable.TYPE_CLUB ->{
                                tempHomeDatas.add(BoardPagePosts(type = a.type, clubPostData = a.clubData, boardPostShowPosition = ConstVariable.DB_TRENDPOST))
                            }
                            ConstVariable.TYPE_COMMUNITY->{
                                tempHomeDatas.add(BoardPagePosts(type = a.type, boardPostItem = a.data, boardPostShowPosition = ConstVariable.DB_TRENDPOST))
                            }
                        }

                    }
                    //DB에 insert
                    if (boardPagePostDao.insertItemList(tempHomeDatas).isNotEmpty()) {
                        //insert 성공 후 리스트 반환
                        return boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_TRENDPOST)
                    }else{
                        return listOf()
                    }
                }
                else -> return listOf()
            }
        }
//        val safeRes = safeApiCall(Dispatchers.IO) { MainHomeRetrofit.homeApiOld.getHomeData() }
//        when (safeRes) {
//            is ResultWrapper.Success -> {
//                if (safeRes.data.code == "200") {
//                    val tempHomeDatas = ArrayList<BoardPagePosts>()
//                    if (page == 0) {
//                        //첫 데이터받기 전 DB 비우기
//                        boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_TRENDPOST)
//                    }
//                    for ((index, a) in safeRes.data.dataObj.list.withIndex()) {
//                        a.db_Type = ConstVariable.DB_TRENDPOST
//                        tempHomeDatas.add(a)
//                    }
//                    //DB에 insert
//                    if (boardPagePostDao.insertAllItem(tempHomeDatas.toList()).isNotEmpty()) {
//                        //insert 성공 후 리스트 반환
//                        return boardPagePostDao.getAllItemOfType(ConstVariable.DB_TRENDPOST)
//                    }
//                }
//            }
//            else -> return listOf()
//        }
        return listOf()
    }
}