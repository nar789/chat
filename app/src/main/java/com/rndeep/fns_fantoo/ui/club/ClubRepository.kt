package com.rndeep.fns_fantoo.ui.club

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.rndeep.fns_fantoo.data.local.dao.BoardPagePostDao
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.data.local.dao.RecommendClubDao
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.api.PostService
import com.rndeep.fns_fantoo.data.remote.dto.ClubCategoryResponse
import com.rndeep.fns_fantoo.data.remote.dto.ClubSubCategoryResponse
import com.rndeep.fns_fantoo.data.remote.dto.ComposeClubPost
import com.rndeep.fns_fantoo.data.remote.model.*
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ClubRepository @Inject constructor(
    private val boardPagePostDao: BoardPagePostDao,
    private val recommendClubDao: RecommendClubDao,
    @NetworkModule.ApiServer private val clubService: ClubService,
    @NetworkModule.ApiServer private val postService: PostService
) : PostRepository(boardPagePostDao, postService) {

    var initClubList: ArrayList<BoardPagePosts> = arrayListOf()
    suspend fun getClubPostData(uId: String): Triple<List<BoardPagePosts>, String?, ErrorBody?> {
        initClubList.add(
            BoardPagePosts(
                type = ConstVariable.TYPE_CLUB_TOP_10,
                boardPostShowPosition = ConstVariable.DB_CLUBPAGE
            )
        )

        safeApiCall(Dispatchers.IO) {
            clubService.fetchTop10PostList(uId)
        }.run {
            var errorCode: String? = null
            var errorMessage: String? = null
            var errorData: ErrorData? = null
            var postDate: String? = null
            when (this) {
                is ResultWrapper.Success -> {
                    boardPagePostDao.deleteAllItemOfShowType(ConstVariable.DB_CLUBPAGE)
                    postDate = this.data.date
                    if (this.data.postList.isEmpty()) {
                        initClubList.add(
                            BoardPagePosts(
                                type = ConstVariable.TYPE_NO_LIST,
                                boardPostShowPosition = ConstVariable.DB_CLUBPAGE
                            )
                        )
                    } else {
                        this.data.postList.forEach {
                            initClubList.add(
                                BoardPagePosts(
                                    type = ConstVariable.TYPE_CLUB,
                                    clubPostData = it,
                                    boardPostShowPosition = ConstVariable.DB_CLUBPAGE
                                )
                            )
                        }
                    }
                }
                is ResultWrapper.GenericError -> {
                    errorCode = this.code
                    errorMessage = this.message
                    errorData = this.errorData
                }
                is ResultWrapper.NetworkError -> {
                    errorCode = ConstVariable.ERROR_NETWORK
                }
            }
            return if (boardPagePostDao.insertItemList(initClubList).isNotEmpty()) {
                Triple(
                    boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_CLUBPAGE),
                    postDate,
                    null
                )
            } else {
                Triple(
                    boardPagePostDao.getAllBoardPostItemOfShowType(ConstVariable.DB_CLUBPAGE),
                    postDate,
                    ErrorBody(errorCode, errorMessage, errorData)
                )
            }
        }
    }

    //내 클럽 리스트
    suspend fun getMyClubList(uId: String?, nextId: String?): Pair<List<MyClubListItem>, String> {
        initClubList = arrayListOf()
        initClubList.add(
            BoardPagePosts(
                type = ConstVariable.TYPE_MY_CLUB,
                boardPostShowPosition = ConstVariable.DB_CLUBPAGE
            )
        )
        return if (uId != null) {
            safeApiCall(Dispatchers.IO) { clubService.getMyClubList(uId, nextId, 10) }.run {
                when (this) {
                    is ResultWrapper.Success -> {
                        if (this.data.clubList.isEmpty()) {
                            Pair(listOf(), this.data.nextId ?: "-1")
                        } else {
                            Pair(this.data.clubList, this.data.nextId ?: "-1")
                        }
                    }
                    else -> {
                        Pair(listOf(), "-1")
                    }
                }
            }
        } else {
            Pair(listOf(), "-1")
        }
    }

    //클럽 챌린지
    suspend fun getChallengeItem(uId: String, nextId: Int?): List<ClubChallengeItem> {
        safeApiCall(Dispatchers.IO) {
            clubService.getChallengePost(
                uId,
                2,
                nextId
            )
        }.run {
            initClubList.add(
                BoardPagePosts(
                    type = ConstVariable.TYPE_CLUB_CHALLENGE,
                    boardPostShowPosition = ConstVariable.DB_CLUBPAGE
                )
            )
            return when (this) {
                is ResultWrapper.Success -> {
                    return this.data.postList
                }
                else -> {
                    return listOf(
                        ClubChallengeItem(
                            0,
                            "",
                            "noChallenge",
                            "",
                            "",
                            "",
                            "",
                            0,
                            null,
                            0,
                            ConstVariable.CHALLENGE_NO_ITEM,
                        )
                    )
                }
            }
        }

    }

    //인기 클럽 카테고리
    suspend fun getHotClubCategory(uId: String) =
        safeApiCall(Dispatchers.IO) {
            if (uId.isEmpty()) {
                clubService.fetchGuestHotClubRecommendCategoryList()
            } else {
                clubService.fetchHotClubRecommendCategoryList(uId)
            }
        }

    //추천 클럽
    suspend fun getRecommendClub(
        type: String,
        uId: String,
        categoryCode: String?
    ): Pair<CommonRecommendSelect?, ErrorBody?> {
        var recommendClubRes: ResultWrapper<RecommendationClubResponse>? = null
        when (type) {
            ConstVariable.DB_CLUBPAGE_HOT -> {
                recommendClubRes =
                    safeApiCall(Dispatchers.IO) {
                        if (uId.isEmpty()) {
                            clubService.fetchGuestPopularRecommendCategoryClubs(categoryCode)
                        } else {
                            clubService.fetchPopularRecommendCategoryClubs(
                                uId,
                                categoryCode
                            )
                        }
                    }
            }
            ConstVariable.DB_CLUBPAGE_NEW -> {
                recommendClubRes =
                    safeApiCall(Dispatchers.IO) {
                        if (uId.isEmpty()) {
                            clubService.fetchGuestNewRecommendClubs()
                        } else {
                            clubService.fetchNewRecommendClubs(uId)
                        }
                    }
            }
        }
        recommendClubRes ?: return Pair(
            null,
            ErrorBody(ConstVariable.ERROR_NETWORK, null, null)
        )
        return when (recommendClubRes) {
            is ResultWrapper.Success -> {
                recommendClubDao.deleteAllItem(type)
                if (recommendClubRes.data.clubList.isEmpty()) {
                    Pair(null, null)
                } else {
                    val recommendClub = CommonRecommendClub()
                    recommendClub.pageType = type
                    recommendClub.recommendClubList = recommendClubRes.data.clubList
                    recommendClubDao.interItems(recommendClub).let {
                        return if (it.isNotEmpty()) {
                            val postType = if (type == ConstVariable.DB_CLUBPAGE_HOT) {
                                ConstVariable.TYPE_CLUB_HOT_RECOMMEND
                            } else {
                                ConstVariable.TYPE_CLUB_NEW_RECOMMEND
                            }
                            initClubList.add(
                                BoardPagePosts(
                                    type = postType,
                                    boardPostShowPosition = ConstVariable.DB_CLUBPAGE
                                )
                            )
                            Pair(recommendClubDao.getRecommendClubAllItemOfType(type), null)
                        } else {
                            Pair(null, null)
                        }
                    }
                }

            }
            is ResultWrapper.GenericError -> {
                Pair(
                    null,
                    ErrorBody(
                        recommendClubRes.code,
                        recommendClubRes.message,
                        recommendClubRes.errorData
                    )
                )
            }
            is ResultWrapper.NetworkError -> {
                Pair(null, ErrorBody(ConstVariable.ERROR_NETWORK, null, null))
            }
        }
    }


    suspend fun fetchMyClubs(
        integUid: String,
        nextId: String?,
        size: String?,
        sort: String?
    ): ResultWrapper<MyClubResponse> = safeApiCall(Dispatchers.IO) {
        clubService.fetchMyClubs(integUid, nextId, size, sort)
    }

    suspend fun fetchClubOneDepthCategories(
        clubId: String,
        integUid: String
    ): ResultWrapper<ClubCategoryResponse> = safeApiCall(Dispatchers.IO) {
        clubService.fetchClubOneDepthCategories(clubId, integUid)
    }

    suspend fun fetchClubTwoDepthCategories(
        clubId: String,
        categoryCode: String,
        integUid: String
    ): ResultWrapper<ClubSubCategoryResponse> = safeApiCall(Dispatchers.IO) {
        clubService.fetchClubTwoDepthCategories(clubId, categoryCode, integUid)
    }

    suspend fun composeClubPost(
        accessToken: String,
        composeClubPost: ComposeClubPost,
        clubId: String,
        categoryCode: String
    ) = safeApiCall(Dispatchers.IO) {
        clubService.composeClubPost(accessToken, composeClubPost, clubId, categoryCode)
    }
}