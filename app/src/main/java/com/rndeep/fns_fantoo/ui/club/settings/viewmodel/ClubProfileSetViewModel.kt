package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubAllInfoResponse
import com.rndeep.fns_fantoo.data.remote.dto.ClubCheckNameResponse
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClubProfileSetViewModel @Inject constructor(
    private val clubSettingRepository: ClubSettingRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    var _clubAllInfoLiveData = MutableLiveData<ClubAllInfoResponse>()
    val clubAllInfoLiveData: LiveData<ClubAllInfoResponse>
        get() = _clubAllInfoLiveData

    fun getClubAllInfo(clubId: String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.getClubAllInfo(clubId, uid)
        when (response) {
            is ResultWrapper.Success -> {
                _clubAllInfoLiveData.value =
                    ClubAllInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError -> {
                _clubAllInfoLiveData.value = ClubAllInfoResponse(
                    response.code ?: "",
                    response.message ?: "",
                    null,
                    response.errorData
                )
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    var _currentClubNameLiveData = MutableLiveData<String>()
    val currentClubNameLiveData: LiveData<String>
        get() = _currentClubNameLiveData

    var _clubNameCheckLiveData = MutableLiveData<ClubCheckNameResponse?>()
    val clubNameCheckLiveData: LiveData<ClubCheckNameResponse?>
        get() = _clubNameCheckLiveData

    fun checkClubName(clubNameText: String) = viewModelScope.launch {
        val response = clubSettingRepository.checkClubName(clubNameText)
        when (response) {
            is ResultWrapper.Success -> {
                _clubNameCheckLiveData.value = ClubCheckNameResponse(
                    ConstVariable.RESULT_SUCCESS_CODE,
                    "",
                    response.data,
                    null
                )
            }
            is ResultWrapper.GenericError -> {
                _clubNameCheckLiveData.value = ClubCheckNameResponse(
                    response.code ?: "",
                    response.message ?: "",
                    null,
                    response.errorData
                )
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }


    private var _clubAllInfoSaveResultLiveData = MutableLiveData<ClubAllInfoResponse?>()
    val clubAllInfoSaveResultLiveData: LiveData<ClubAllInfoResponse?>
        get() = _clubAllInfoSaveResultLiveData

    fun setClubAllInfo(clubId: String, reqData: HashMap<String, Any>) = viewModelScope.launch {
        val response = clubSettingRepository.setClubAllInfo(clubId, reqData)
        loadingStatusLiveData.value = false
        when (response) {
            is ResultWrapper.Success -> {
                _clubAllInfoSaveResultLiveData.value =
                    ClubAllInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError -> {
                _clubAllInfoSaveResultLiveData.value =
                    ClubAllInfoResponse(response.code!!, response.message!!, null, response.errorData)
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    var _clubBgImageUriLiveData = SingleLiveEvent<Uri>()
    val clubBgImageUriLiveData: LiveData<Uri>
        get() = _clubBgImageUriLiveData

    private var _clubBgImageUploadResultLiveData = SingleLiveEvent<String>()
    val clubBgImageUploadResultLiveData: LiveData<String>
        get() = _clubBgImageUploadResultLiveData

    fun uploadBgImage(cloudFlareKey: String, fileMultipartBody: MultipartBody.Part) =
        viewModelScope.launch {
            loadingStatusLiveData.value = true
            uploadRepository.fileUploadImageToCloudFlare(cloudFlareKey, fileMultipartBody).run {
                Timber.d("fileUploadImageToCloudFlare $this")
                if(this == null){
                    loadingStatusLiveData.value = false
                }else {
                    _clubBgImageUploadResultLiveData.value = this
                }
            }
        }

    var _clubProfileImageUriLiveData = SingleLiveEvent<Uri>()
    val clubProfileImageUriLiveData: LiveData<Uri>
        get() = _clubProfileImageUriLiveData

    private var _clubProfileImageUploadResultLiveData = SingleLiveEvent<String>()
    val clubProfileImageUploadResultLiveData: LiveData<String>
        get() = _clubProfileImageUploadResultLiveData

    fun uploadClubProfileImage(cloudFlareKey: String, fileMultipartBody: MultipartBody.Part) =
        viewModelScope.launch {
            loadingStatusLiveData.value = true
            uploadRepository.fileUploadImageToCloudFlare(cloudFlareKey, fileMultipartBody).run {
                Timber.d("fileUploadImageToCloudFlare $this")
                if (this == null) {
                    loadingStatusLiveData.value = false
                } else {
                    _clubProfileImageUploadResultLiveData.value = this
                }
            }
        }

    fun deleteCloudFlareImage(cloudFlareKey: String, imageId:String) = viewModelScope.launch {
        val response = uploadRepository.fileDeleteImage(cloudFlareKey, imageId)
        Timber.d("deleteCloudFlareImage result $response")
    }
}
