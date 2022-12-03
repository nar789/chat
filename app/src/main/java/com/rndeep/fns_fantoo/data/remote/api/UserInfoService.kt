package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.*
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.userinfo.PasswordValidator
import retrofit2.http.*

interface UserInfoService {

    @POST("my/referral/use")
    suspend fun registerReferralCode(
        @Header("access_token") accessToken: String,
        @Body referral: HashMap<String, String>
    )

    @GET("/user/check/nickname")
    suspend fun checkNickname(
        @Query("nickname") nickname: String,
    ): CheckNicknameResponse

    @POST("/user/info")
    suspend fun fetchUserInfo(
        @Header("access_token") accessToken: String,
        @Body integUid: IntegUid
    ): UserInfoResponse

    @POST("user/info/update/{userInfoType}")
    suspend fun updateUserInfoByType(
        @Header("access_token") accessToken: String,
        @Body userInfo: UserInfoResponse,
        @Path("userInfoType") type: String,
    )

    @POST("my/user/pass/check")
    suspend fun checkPassword(
        @Header("access_token") accessToken: String,
        @Body passwordValidator: PasswordValidator
    ): CheckPasswordResponse

    @POST("my/user/pass/update")
    suspend fun updatePassword(
        @Header("access_token") accessToken: String,
        @Body passwordValidator: PasswordValidator
    )

    @POST("user/logout")
    suspend fun logout(
        @Header("access_token") accessToken: String,
        @Body integUid: IntegUid
    )

    @GET("user/interest/category")
    suspend fun fetchInterestCategory(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
    ): InterestCategoryResponse

    @GET("my/user/wallet")
    suspend fun fetchUserWallet(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
    ): MyWalletResponse

    @GET("my/user/wallet/{walletType}")
    suspend fun fetchUserWalletHistory(
        @Header("access_token") accessToken: String,
        @Path("walletType") walletType: String,
        @Query("integUid") integUid: String,
        @Query("walletListType") walletListType: String,
        @Query("yearMonth") yearMonth: String,
        @Query("nextId") nextId: Int,
        @Query("size") size: Int,
    ): MyWalletHistoryResponse

    @GET("my/write/count")
    suspend fun fetchMyWriteCount(
        @Header("access_token") accessToken: String,
        @Query("integUid") integUid: String,
    ): MyWriteCountResponse

}