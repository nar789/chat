package com.rndeep.fns_fantoo.ui.menu.editprofile

import android.app.Application
import androidx.lifecycle.*
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.CountryResponse
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.userinfo.UserInfoType
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.ui.login.CommonRepository
import com.rndeep.fns_fantoo.ui.menu.MenuRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val application: Application,
    private val menuRepository: MenuRepository,
    private val userInfoRepository: UserInfoRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val commonRepository: CommonRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {

    private val _state = MutableStateFlow<EditProfileUIState>(EditProfileUIState.None)
    val state = _state.asStateFlow()

    private var _editProfileItem = MutableLiveData<List<EditProfileItem>>()
    val editProfileItem: LiveData<List<EditProfileItem>>
        get() = _editProfileItem

    val myProfile = menuRepository.getMyProfile().asLiveData()

    private val nicknameItem = EditProfileItem(EditProfileItemType.NICKNAME, null, true)
    private val concernItem = EditProfileItem(EditProfileItemType.CONCERN, null, true)
    private val countryItem = EditProfileItem(EditProfileItemType.COUNTRY, null, false)
    private val genderItem = EditProfileItem(EditProfileItemType.GENDER, null, false)
    private val birthdayItem = EditProfileItem(EditProfileItemType.BIRTHDAY, null, false)

    private val editProfileItems = listOf(nicknameItem, concernItem, countryItem, genderItem, birthdayItem)

    private lateinit var accessToken: String
    private lateinit var integUid: String
    lateinit var userInfo: UserInfoResponse
    private var country: CountryResponse? = null
    private var isFirstCompleteProfile = false

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchUserInfo()
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_FIRST_PROFILE_COMPLETE)?.let {
                isFirstCompleteProfile = it
            }
            Timber.d("accessToken: $accessToken , integUid: $integUid, firstCompleteProfile: $isFirstCompleteProfile")
        }
        setEditProfileItem()
    }

    private fun setEditProfileItem() {
        _editProfileItem.value = editProfileItems
    }

    fun updateItems(profile: MyProfile) {
        updateNickname(profile.nickname)
        updateConcern(profile.concern)
        updateCountry(profile.country)
        updateGender(profile.gender)
        updateBirthday(profile.birthday)

        val count = checkProfileCompleteCount(profile)
        if((count == COMPLETE_PROFILE_COUNT) && !isFirstCompleteProfile) {
            completedEditProfile()
        }
    }

    fun updateEditProfileState() {
        _state.update {
            EditProfileUIState.Success(EditProfileState.VALID)
        }
    }

    fun updateNickname(nickname: String?) {
        editProfileItems.find { item ->
            item.itemType == EditProfileItemType.NICKNAME
        }!!.value = nickname
        _editProfileItem.value = editProfileItems
    }

    fun updateConcern(concern: String?) {
        editProfileItems.find { item ->
            item.itemType == EditProfileItemType.CONCERN
        }!!.value = concern
        _editProfileItem.value = editProfileItems
    }

    fun updateCountry(country: String?) {
        editProfileItems.find { item ->
            item.itemType == EditProfileItemType.COUNTRY
        }!!.value = country
        _editProfileItem.value = editProfileItems
    }

    fun updateGender(genderType: GenderType) {
        editProfileItems.find { item ->
            item.itemType == EditProfileItemType.GENDER
        }!!.value = application.applicationContext.getString(genderType.stringRes)
        Timber.d("updateGender item : ${editProfileItems[2]}")
        _editProfileItem.value = editProfileItems
    }

    fun updateBirthday(time: String?) {
//        val birthday = TimeUtils.birthdayString(time, application.resources)
        val birthday = time
        Timber.d("birthday : $birthday")
        editProfileItems.find { item ->
            item.itemType == EditProfileItemType.BIRTHDAY
        }!!.value = birthday
        _editProfileItem.value = editProfileItems
    }

    private fun completedEditProfile() {
        setFirstProfileComplete()
        _state.update {
            EditProfileUIState.Success(EditProfileState.COMPLETED)
        }
    }

    private fun setFirstProfileComplete() = viewModelScope.launch {
        dataStoreRepository.putBoolean(DataStoreKey.PREF_KEY_IS_FIRST_PROFILE_COMPLETE, true)
    }

    private fun updateProfileImage(imageId: String) = viewModelScope.launch {
        userInfo.userPhoto = imageId
        val response = userInfoRepository.updateUserInfoByType(accessToken, userInfo, UserInfoType.USERPHOTO.type)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("success")
                userInfoRepository.updateImage(imageId)
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

    fun updateProfileCountry(country: Country) = viewModelScope.launch {
        userInfo.countryIsoTwo = country.iso2
        val response = userInfoRepository.updateUserInfoByType(accessToken, userInfo, UserInfoType.COUNTRY.type)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("success")
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

    fun updateProfileGender(gender: GenderType) = viewModelScope.launch {
        userInfo.genderType = if(gender == GenderType.MALE) "male" else "female"
        val response = userInfoRepository.updateUserInfoByType(accessToken, userInfo, UserInfoType.GENDER.type)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("success")
                userInfoRepository.updateGender(gender)
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

    fun updateProfileBirthday(date: String) = viewModelScope.launch {
        userInfo.birthDay = date
        val response = userInfoRepository.updateUserInfoByType(accessToken, userInfo, UserInfoType.BIRTH.type)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("success")
                userInfoRepository.updateBirthday(date)
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

    private fun checkProfileCompleteCount(profile: MyProfile) : Int {
        var count = MINIMUM_PROFILE_COUNT
        if(!profile.imageUrl.isNullOrEmpty()) count++
        if(profile.gender != GenderType.UNKNOWN) count++
        if(!profile.concern.isNullOrEmpty()) count++
        if(!profile.birthday.isNullOrEmpty()) count++
        Timber.d("check profile count : $count")

        return count
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

    fun uploadImage(file: MultipartBody.Part) = viewModelScope.launch {
        val cloudFlareKey = application.applicationContext.getString(R.string.cloudFlareKey)
        val response = uploadRepository.imageUploadToCloudFlare(cloudFlareKey, file)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                val resultData = response.data
                if(resultData.success) {
                    updateProfileImage(resultData.result.id)
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

    fun getCountry() = country

    companion object {
        const val MINIMUM_PROFILE_COUNT = 2
        const val COMPLETE_PROFILE_COUNT = 6
    }

}