package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubCheckNickNameDto
import com.rndeep.fns_fantoo.data.remote.dto.ClubCheckNickNameResponse
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.data.remote.model.ClubMemberInfo
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.asMultipart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class ClubMyProfileViewModel @Inject constructor(
    private val clubSettingRepository: ClubSettingRepository,
    private val uploadRepository: UploadRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    var _nickNameCheckLiveData = SingleLiveEvent<ClubCheckNickNameResponse>()
    val nickNameCheckLiveData: LiveData<ClubCheckNickNameResponse>
        get() = _nickNameCheckLiveData

    fun checkProfileNickName(clubId: String, nickName: String) = viewModelScope.launch {
        val response = clubSettingRepository.checkClubNickName(clubId, nickName)
        when (response) {
            is ResultWrapper.Success -> {
                _nickNameCheckTokenLiveData.value = response.data.checkToken
                _nickNameCheckLiveData.value =
                    ClubCheckNickNameResponse(
                        ConstVariable.RESULT_SUCCESS_CODE,
                        "",
                        response.data,
                        null
                    )
            }
            is ResultWrapper.GenericError -> {
                _nickNameCheckLiveData.value = ClubCheckNickNameResponse(
                    response.code!!,
                    response.message!!,
                    null,
                    response.errorData
                )
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    var _currentNickNameLiveData = MutableLiveData<String>()
    val currentNickNameLiveData: LiveData<String>
        get() = _currentNickNameLiveData

    var _nickNameCheckTokenLiveData = MutableLiveData<String>()
    val nickNameCheckTokenLiveData: LiveData<String>
        get() = _nickNameCheckTokenLiveData

    var _uploadFileImageUriLiveData = MutableLiveData<Uri>()
    val uploadFileImageUriLiveData: LiveData<Uri>
        get() = _uploadFileImageUriLiveData

    private var _profileImageUploadResultLiveData = SingleLiveEvent<String?>()
    val profileImageUploadResultLiveData: LiveData<String?>
        get() = _profileImageUploadResultLiveData

    fun uploadProfileImage(cloudFlareKey: String, fileMultipartBody: MultipartBody.Part) =
        viewModelScope.launch {
            loadingStatusLiveData.value = true
            uploadRepository.fileUploadImageToCloudFlare(cloudFlareKey, fileMultipartBody).run {
                Timber.d("fileUploadImageToCloudFlare $this")
                if(this == null){
                    loadingStatusLiveData.value = false
                }else {
                    _profileImageUploadResultLiveData.value = this
                }
            }
        }

    fun deleteProfileImage(cloudFlareKey: String, imageId:String) = viewModelScope.launch {
        val response = uploadRepository.fileDeleteImage(cloudFlareKey, imageId)
        Timber.d("deleteProfileImage result $response")
    }

    private var _saveProfileResultLiveData = SingleLiveEvent<BaseResponse>()
    val saveProfileResultLiveData: LiveData<BaseResponse>
        get() = _saveProfileResultLiveData

    fun saveProfile(
        clubId: String,
        uid: String,
        nickName: String,
        nickCheckToken: String?,
        profileImg: String?
    ) =
        viewModelScope.launch {
            val requestMap = HashMap<String, String>()
            requestMap[KEY_UID] = uid
            if (!nickCheckToken.isNullOrEmpty()) {
                requestMap["nickname"] = nickName
                requestMap["checkToken"] = nickCheckToken
            }
            if (!profileImg.isNullOrEmpty()) {
                requestMap["profileImg"] = profileImg
            }
            val response = clubSettingRepository.modifyMyProfileOfClub(
                clubId, requestMap
            )
            loadingStatusLiveData.value = false
            when (response) {
                is ResultWrapper.Success -> {
                    _saveProfileResultLiveData.value =
                        BaseResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data)
                }
                is ResultWrapper.GenericError -> {
                    _saveProfileResultLiveData.value =
                        BaseResponse(response.code!!, response.message!!, response.errorData!!)
                }
                is ResultWrapper.NetworkError -> {
                    _networkErrorLiveData.value = true
                }
            }
        }
}
