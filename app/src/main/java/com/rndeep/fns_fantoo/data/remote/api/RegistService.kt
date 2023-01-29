package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.UserNickCheckDto
import com.rndeep.fns_fantoo.data.remote.model.VerifyCodeCheckResponse
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_LOGIN_ID
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_AUTHCODE
import com.rndeep.fns_fantoo.utils.ConstVariable.UserInfo.Companion.KEY_NICKNAME
import retrofit2.http.*

interface RegistService {

    //회원가입, 인증번호 발송
    @GET("/user/join/email/send")
    suspend fun requestVerifyCodeToEmail(
        @Query(KEY_LOGIN_ID) email:String
    ): Unit

    @GET("/user/join/email/auth/check")
    suspend fun checkVerifyCode(
        @Query(KEY_AUTHCODE) authCode:String,
        @Query(KEY_LOGIN_ID) email:String
    ): VerifyCodeCheckResponse

    @GET("/user/check/nickname")
    suspend fun checkUserNickName(
        @Query(KEY_NICKNAME) nickName:String
    ): UserNickCheckDto

    @POST("/user/sns/join")
    suspend fun joinBySns(
        @Body() value:HashMap<String,String>
    ): Unit

    @POST("/user/email/join")
    suspend fun joinByEmail(
        @Body() value:HashMap<String,String>
    ): Unit

}