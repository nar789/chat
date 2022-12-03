package com.rndeep.fns_fantoo.ui.regist

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.RegistService
import com.rndeep.fns_fantoo.data.remote.dto.UserNickCheckDto
import com.rndeep.fns_fantoo.data.remote.model.RegistResponse
import com.rndeep.fns_fantoo.data.remote.dto.UserNickCheckResponse
import com.rndeep.fns_fantoo.data.remote.model.VerifyCodeCheckResponse
import com.rndeep.fns_fantoo.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class RegistRepository @Inject constructor(@NetworkModule.ApiServer private val service: RegistService)
    : BaseNetRepo(){

    suspend fun requestVerifyCodeToEmail(email: String):
            ResultWrapper<Unit> = safeApiCall(
        Dispatchers.IO
    ) {
        service.requestVerifyCodeToEmail(email)
    }

    suspend fun checkVerifyCode(authCode : String, email : String):
            ResultWrapper<VerifyCodeCheckResponse> = safeApiCall(
                Dispatchers.IO
            ){
                service.checkVerifyCode(authCode, email)
    }

    suspend fun checkUserNickname(nickName:String) :
            ResultWrapper<UserNickCheckDto> = safeApiCall(
                Dispatchers.IO
            ){
                service.checkUserNickName(nickName)
    }

    suspend fun joinBySns(value:HashMap<String,String>):
            ResultWrapper<Unit> = safeApiCall(
                Dispatchers.IO
            ){
                service.joinBySns(value)
    }

    suspend fun joinByEmail(value:HashMap<String,String>):
            ResultWrapper<Unit> = safeApiCall(
        Dispatchers.IO
    ){
        service.joinByEmail(value)
    }
}