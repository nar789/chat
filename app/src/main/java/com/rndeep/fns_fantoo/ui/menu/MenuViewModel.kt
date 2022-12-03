package com.rndeep.fns_fantoo.ui.menu

import android.app.Application
import androidx.lifecycle.*
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.CountryResponse
import com.rndeep.fns_fantoo.data.remote.dto.MyWriteCountResponse
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.userinfo.Interest
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.ui.login.CommonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val application: Application,
    private val menuRepository: MenuRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val userInfoRepository: UserInfoRepository,
    private val commonRepository: CommonRepository
) : ViewModel() {

    val myProfile = menuRepository.getMyProfile().asLiveData()

    private val _menus = MutableLiveData<List<MenuItem>>()
    val menus: LiveData<List<MenuItem>> = _menus

    private val _contents = MutableLiveData<List<MenuItem>>()
    val contents: LiveData<List<MenuItem>> = _contents

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String>
        get() = _nickname

    private val myClub = MenuItem.Item(MenuItemType.MY_CLUB, null, IconType.IMAGE)
    private val myWallet = MenuItem.Item(MenuItemType.MY_WALLET, null, IconType.IMAGE)
    private val inviteFriend = MenuItem.Item(MenuItemType.INVITE_FRIEND, null, IconType.IMAGE)

    private val event = MenuItem.Item(MenuItemType.EVENT, null, IconType.IMAGE)
    private val fantooTv = MenuItem.Item(MenuItemType.FANTOO_TV, null, IconType.IMAGE)
    private val hanryuTimes = MenuItem.Item(MenuItemType.HANRYU_TIMES, null, IconType.IMAGE)

    private val myMenuItems = listOf(myClub, myWallet, inviteFriend)
    private val contentsItems = listOf(event, fantooTv, hanryuTimes)

    private val _myWriteCount = MutableLiveData<MyWriteCountResponse?>()
    val myWriteCount: MutableLiveData<MyWriteCountResponse?> = _myWriteCount

    private lateinit var accessToken: String
    private lateinit var integUid: String
    private lateinit var userInfo: UserInfoResponse
    private lateinit var country: CountryResponse
    var isLogin = false

    init {
        Timber.d("init")
        _menus.value = myMenuItems
        _contents.value = contentsItems
        viewModelScope.launch {
            accessToken =
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isLogin = it
            }
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchUserInfo()
            fetchMyWriteCount()
        }
    }

    private fun fetchUserInfo() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(integUid))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                userInfo = response.data
                userInfo.countryIsoTwo?.let {
                    fetchSelectCountryByIso2(it)
                }
                userInfoRepository.insertProfile(
                    MyProfile(
                        userInfo.integUid,
                        userInfo.userPhoto,
                        userInfo.userNick,
                        getCodeList(userInfo.interestList),
                        userInfo.countryIsoTwo,
                        getGenderType(userInfo.genderType.toString()),
                        userInfo.birthDay,
                    )
                )
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun fetchSelectCountryByIso2(iso2: String) = viewModelScope.launch {
        val response = commonRepository.getSelectCountry(iso2)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                country = response.data
                if(userInfo.langCode.toString() == "kr") {
                    userInfoRepository.updateCountry(country.nameKr)
                } else {
                    userInfoRepository.updateCountry(country.nameEn)
                }
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun fetchMyWriteCount() = viewModelScope.launch {
        val response = userInfoRepository.fetchMyWriteCount(accessToken, integUid)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _myWriteCount.value = response.data
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun getGenderType(genderType: String): GenderType {
        return when (genderType) {
            "male" -> GenderType.MALE
            "female" -> GenderType.FEMALE
            else -> GenderType.UNKNOWN
        }
    }

    private fun getCodeList(list: List<Interest>) : String? {
        val codeList = mutableListOf<String>()
        list.forEach {
            codeList.add(it.code)
        }
        Timber.d("codeList: $codeList")
        return if(codeList.size == 0) {
            null
        } else {
            codeList.toString()
        }
    }

    fun getMyReferralCode() = userInfo.referralCode
}