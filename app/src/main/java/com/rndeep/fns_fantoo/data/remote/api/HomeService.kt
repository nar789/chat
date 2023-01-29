package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.model.HomeAlarmResponse
import com.rndeep.fns_fantoo.data.remote.model.RecommendClubData
import com.rndeep.fns_fantoo.data.remote.model.RecommendationClubResponse
import com.rndeep.fns_fantoo.utils.ConstVariable
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {

    @GET("/club/main/popular/user/club/top10")
    suspend fun getSuitRecommendClub(
        @Query(ConstVariable.KEY_UID) uid: String?,
    ) : RecommendationClubResponse

    //인기 클럽 TOP 10
    @GET("/club/main/popular/club/top10")
    suspend fun getHotRecommendClub(
        @Query(ConstVariable.KEY_UID) uid: String?,
    ) : RecommendationClubResponse

    //인기 클럽 TOP 10 (비로그인)
    @GET("/club/main/guest/popular/club/top10")
    suspend fun getGuestHotRecommendClub(
    ) : RecommendationClubResponse

    //HomeAlarm List Api
    @GET("/club/alim")
    suspend fun getAlarmList(
        @Query(ConstVariable.KEY_UID) uId :String,
        @Query("nextId") nextId : String?,
        @Query("size") size :String
    ) :HomeAlarmResponse

}