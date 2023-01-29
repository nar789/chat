package com.rndeep.fns_fantoo.ui.club.create

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.ClubCreateSendItem
import com.rndeep.fns_fantoo.data.remote.dto.ClubInterestCategoryDto
import com.rndeep.fns_fantoo.data.remote.model.club.ClubInterestItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.asMultipart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ClubCreateInterestViewModel @Inject constructor(
    private val createRepository: ClubCreateRepository,
    private val uploadRepository: UploadRepository,
    private val dataStoreRepository: DataStoreRepository
)  :ViewModel() {

    private var uId :String? =null
    init {
        viewModelScope.launch {
            uId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
        }
    }

    private val _errorCodeLiveData = MutableLiveData<String>()
    val errorCodeLiveData : LiveData<String> get() = _errorCodeLiveData

    private val _categoryItemLiveData = MutableLiveData<List<ClubInterestCategoryDto>>()
    val categoryItemLiveData :LiveData<List<ClubInterestCategoryDto>> get() = _categoryItemLiveData
    fun getCategoryItems(){
        viewModelScope.launch {
            createRepository.fetchClubCreateCategory().run {
                if(this.second!=null){
                    this.second
                }else{
                    _categoryItemLiveData.value=this.first?: listOf()
                }
            }
        }
    }

    private val _loadingCreateClubLiveData = SingleLiveEvent<Boolean>()
    val loadingCreateClubLiveData :LiveData<Boolean> get() = _loadingCreateClubLiveData

    private val _clubCreateResultLiveData = MutableLiveData<String?>()
    val clubCreateResultLiveData :LiveData<String?> get() = _clubCreateResultLiveData
    fun callCreateClub(createClubInfo : ClubCreateSendItem, favoriteCategory : Int,context : Context){
        viewModelScope.launch {
            if(uId==null){
                return@launch
            }
            _loadingCreateClubLiveData.value=true
            val cloudKey =context.getString(R.string.cloudFlareKey)
            val bannerImageId=if(createClubInfo.bannerImage!=null){
                uploadRepository.fileUploadImageToCloudFlare(cloudKey,createClubInfo.bannerImage)
            }else{
                createClubInfo.bannerDefault
            }
            if(bannerImageId==null){
                _loadingCreateClubLiveData.value=false
                _errorCodeLiveData.value = ConstVariable.ERROR_IMAGE_UPLOAD_FAIL
                return@launch
            }
            val profileImageId=if(createClubInfo.profileImage!=null){
                uploadRepository.fileUploadImageToCloudFlare(cloudKey,createClubInfo.profileImage)
            }else{
                createClubInfo.profileDefault
            }
            if(profileImageId==null){
                _loadingCreateClubLiveData.value=false
                _errorCodeLiveData.value = ConstVariable.ERROR_IMAGE_UPLOAD_FAIL
                return@launch
            }
            createRepository.requestCreateClub(
                createClubInfo.majorCountry,
                bannerImageId,
                createClubInfo.checkToken,
                createClubInfo.clubName,
                uId!!,
                favoriteCategory,
                createClubInfo.majorLang,
                createClubInfo.acceptMethod,
                createClubInfo.openMethod,
                profileImageId
            ).run {
                _clubCreateResultLiveData.value=this
            }
            _loadingCreateClubLiveData.value=false
        }
    }

    suspend fun upLoadBannerImageId(bannerImageMultipartBody:MultipartBody.Part, contextResolver: ContentResolver, cloudKey:String) =
           uploadRepository.fileUploadImageToCloudFlare(cloudKey,bannerImageMultipartBody)

    suspend fun uploadProfileImageId(profileImageMultipartBody:MultipartBody.Part,contextResolver: ContentResolver,cloudKey: String)=
            uploadRepository.fileUploadImageToCloudFlare(cloudKey,profileImageMultipartBody)

}