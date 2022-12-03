package com.rndeep.fns_fantoo.repositories

import com.rndeep.fns_fantoo.data.local.dao.MyProfileDao
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.UserInfoService
import com.rndeep.fns_fantoo.data.remote.dto.*
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.userinfo.PasswordValidator
import com.rndeep.fns_fantoo.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class UserInfoRepository @Inject constructor(
    @NetworkModule.ApiServer private val service: UserInfoService,
    private val myProfileDao: MyProfileDao,
) : BaseNetRepo() {

    // Remote
    suspend fun registerReferralCode(
        accessToken: String,
        referral: HashMap<String, String>
    ) = safeApiCall(
        Dispatchers.IO
    ) { service.registerReferralCode(accessToken, referral) }

    suspend fun checkNickname(
        nickname: String
    ): ResultWrapper<CheckNicknameResponse> = safeApiCall(
        Dispatchers.IO
    ) { service.checkNickname(nickname) }

    suspend fun fetchUserInfo(
        accessToken: String,
        integUid: IntegUid
    ): ResultWrapper<UserInfoResponse> = safeApiCall(
        Dispatchers.IO
    ) { service.fetchUserInfo(accessToken, integUid) }

    suspend fun updateUserInfoByType(
        accessToken: String,
        userInfo: UserInfoResponse,
        type: String
    ) = safeApiCall(
        Dispatchers.IO
    ) { service.updateUserInfoByType(accessToken, userInfo, type) }

    suspend fun checkPassword(
        accessToken: String,
        passwordValidator: PasswordValidator,
    ): ResultWrapper<CheckPasswordResponse> = safeApiCall(
        Dispatchers.IO
    ) { service.checkPassword(accessToken, passwordValidator) }

    suspend fun updatePassword(
        accessToken: String,
        passwordValidator: PasswordValidator,
    ) = safeApiCall(
        Dispatchers.IO
    ) { service.updatePassword(accessToken, passwordValidator) }

    suspend fun logout(
        accessToken: String,
        integUid: IntegUid
    ) = safeApiCall(
        Dispatchers.IO
    ) { service.logout(accessToken, integUid) }

    suspend fun fetchInterestCategory(
        accessToken: String,
        integUid: String
    ): ResultWrapper<InterestCategoryResponse> = safeApiCall(
        Dispatchers.IO
    ) { service.fetchInterestCategory(accessToken, integUid) }

    suspend fun fetchUserWallet(
        accessToken: String,
        integUid: String
    ): ResultWrapper<MyWalletResponse> = safeApiCall(
        Dispatchers.IO
    ) { service.fetchUserWallet(accessToken, integUid) }

    suspend fun fetchUserWalletHistory(
        accessToken: String,
        walletType: String,
        integUid: String,
        walletListType: String,
        yearMonth: String,
        nextId: Int,
        size: Int
    ): ResultWrapper<MyWalletHistoryResponse> = safeApiCall(
        Dispatchers.IO
    ) {
        service.fetchUserWalletHistory(
            accessToken,
            walletType,
            integUid,
            walletListType,
            yearMonth,
            nextId,
            size
        )
    }

    suspend fun fetchMyWriteCount(
        accessToken: String,
        integUid: String
    ): ResultWrapper<MyWriteCountResponse> = safeApiCall(
        Dispatchers.IO
    ) { service.fetchMyWriteCount(accessToken, integUid) }


    // Local
    suspend fun updateImage(imageId: String) = myProfileDao.updateImage(imageId)
    suspend fun updateNickname(nickname: String) = myProfileDao.updateNickname(nickname)
    suspend fun updateGender(genderType: GenderType) = myProfileDao.updateGender(genderType)
    suspend fun updateBirthday(date: String) = myProfileDao.updateBirthday(date)
    suspend fun updateCountry(country: String) = myProfileDao.updateCountry(country)
    suspend fun updateInterest(interest: String) = myProfileDao.updateInterest(interest)
    suspend fun insertProfile(profile: MyProfile) = myProfileDao.insert(profile)
    suspend fun deleteProfile() = myProfileDao.deleteProfile()
}