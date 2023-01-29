package com.rndeep.fns_fantoo.ui.login

import com.rndeep.fns_fantoo.data.local.dao.MyProfileDao
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.CommonService
import com.rndeep.fns_fantoo.data.remote.dto.CheckingJoinUserDto
import com.rndeep.fns_fantoo.data.remote.dto.ConfigResponse
import com.rndeep.fns_fantoo.data.remote.dto.CountryResponse
import com.rndeep.fns_fantoo.data.remote.model.*
import com.rndeep.fns_fantoo.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CommonRepository @Inject constructor(
    @NetworkModule.ApiServer private val service: CommonService,
    private val myProfileDao: MyProfileDao
) : BaseNetRepo() {

    suspend fun getCountryAll(
    ): ResultWrapper<List<Country>> = safeApiCall(
        Dispatchers.IO
    ) {
        service.getCountryAll()
    }

    suspend fun getSelectCountry(
        iso2: String
    ): ResultWrapper<CountryResponse> = safeApiCall(
        Dispatchers.IO
    ) { service.getSelectCountry(iso2) }

    suspend fun checkingJoinedUser(
        loginType: String,
        loginId: String
    ): ResultWrapper<CheckingJoinUserDto> = safeApiCall(Dispatchers.IO) {
        service.checkingJoinedUser(
            loginType,
            loginId,
        )
    }

    suspend fun requestTempPassword(
        loginId: String
    ): ResultWrapper<Unit> = safeApiCall(Dispatchers.IO) {
        service.requestTempPassword(loginId)
    }

    suspend fun insertMyProfile(profile: MyProfile) {
        myProfileDao.insert(profile)
    }

    suspend fun getConfig(serviceType: String, deviceType: String): ResultWrapper<ConfigResponse> =
        safeApiCall(Dispatchers.IO)
        {
            service.getConfig(serviceType, deviceType)
        }
}