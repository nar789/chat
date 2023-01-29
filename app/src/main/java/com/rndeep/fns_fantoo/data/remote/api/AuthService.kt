package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.dto.AuthCodeDto
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.dto.AccessTokenDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {

    // Auth
    @POST("oauth2.0/authorize")
    suspend fun getAuthCode(
        @Body values: HashMap<String, String>
    ): AuthCodeDto

    @POST("oauth2.0/token")
    suspend fun getAccessToken(
        @Body values: HashMap<String, String>
    ): AccessTokenDto

}