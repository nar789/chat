package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.model.HomeAlarmResponse
import com.rndeep.fns_fantoo.data.remote.model.RecommendClubData
import com.rndeep.fns_fantoo.utils.ConstVariable
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {

    //Home Tab 맞춤 클럽 아직 HOME 의 API 가 안나와 임시로 CLUBTAB API를 가져옴
    @GET("/club/main/popular/recommendation")
    suspend fun getSuitRecommendClub() : RecommendClubData

    //Home Tab 인기 클럽 아직 HOME 의 API 가 안나와 임시로 CLUBTAB API를 가져옴
    @GET("/club/main/popular/recommendation")
    suspend fun getHotRecommendClub() : RecommendClubData

    //HomeAlarm List Api
    @GET("/club/alim")
    suspend fun getAlarmList(
        @Query(ConstVariable.KEY_UID) uId :String,
        @Query("nextId") nextId : String?,
        @Query("size") size :String
    ) :HomeAlarmResponse

}