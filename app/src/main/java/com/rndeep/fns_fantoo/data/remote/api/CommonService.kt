package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.CheckingJoinUserDto
import com.rndeep.fns_fantoo.data.remote.dto.CheckingJoinUserResponse
import com.rndeep.fns_fantoo.data.remote.dto.ConfigResponse
import com.rndeep.fns_fantoo.data.remote.dto.CountryResponse
import com.rndeep.fns_fantoo.data.remote.model.*
import com.rndeep.fns_fantoo.utils.ConstVariable.Config.Companion.KEY_DEVICE_TYPE
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_LOGIN_ID
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_LOGIN_TYPE
import retrofit2.http.*

interface CommonService {

    @GET("/country/select/all")
    suspend fun getCountryAll(
    ):List<Country>

    @GET("/country/select/iso2")
    suspend fun getSelectCountry(
        @Query("iso2") iso2: String
    ): CountryResponse

    @GET("/lang/all")
    suspend fun getSupportLanguageAll(
    ):List<Language>

    @GET("/user/join/check")
    suspend fun checkingJoinedUser(
        @Query(KEY_LOGIN_TYPE) loginType:String,
        @Query(KEY_LOGIN_ID) loginId:String
    ): CheckingJoinUserDto

    @GET("/user/email/pass/temp")
    suspend fun requestTempPassword(
        @Query(KEY_LOGIN_ID) loginId:String,
    ): Unit

    @GET("/mconfig/{serviceType}")
    suspend fun getConfig(@Path(value = "serviceType", encoded = true) serviceType:String,
    @Query(KEY_DEVICE_TYPE) deviceType:String):ConfigResponse
}