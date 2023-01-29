package com.rndeep.fns_fantoo.ui.login

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.AuthService
import com.rndeep.fns_fantoo.data.remote.dto.AccessTokenDto
import com.rndeep.fns_fantoo.data.remote.dto.AuthCodeDto
import com.rndeep.fns_fantoo.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class AuthRepository @Inject constructor(
    @NetworkModule.AuthServer private val service: AuthService
) : BaseNetRepo() {

    suspend fun getAuthCode(
        values: HashMap<String, String>
    ): ResultWrapper<AuthCodeDto> = safeApiCall(Dispatchers.IO) {
        service.getAuthCode(values)
    }

    suspend fun getAccessToken(
        values: HashMap<String, String>
    ): ResultWrapper<AccessTokenDto> = safeApiCall(
        Dispatchers.IO
    ) {
        service.getAccessToken(values)
    }

}
