package com.rndeep.fns_fantoo.ui.club.join

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.editor.MultimediaType
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.asMultipart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ClubJoinViewModel @Inject constructor(
    private val repository: ClubJoinRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val uploadRepository: UploadRepository
    ) : ViewModel() {

    private var accessToken : String? = null
    private var integUid : String? = null
    private var isUser = false
    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isUser=it
                if(it){
                    accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
                    integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
                }
            }
        }
    }

    var clubName = ""
    private val _clubExistsLiveData = SingleLiveEvent<Boolean>()
    val clubExistsLiveData :LiveData<Boolean> get() = _clubExistsLiveData
    fun getClubInfo(clubId: String){
        viewModelScope.launch {
            if(!isUser){
                _clubExistsLiveData.value=false
                return@launch
            }
            repository.checkClubExists(clubId,integUid!!).run {
                if(this == null){
                    _clubExistsLiveData.value=false
                }else{
                    clubName=this
                }
            }
        }
    }

    val clubJoinResultLiveData = SingleLiveEvent<Boolean?>()
    fun callClubJoinToServer(clubId: String, clubNickName :String, cloudKey :String){
        viewModelScope.launch {
            if(multimediaFile !=null){
                uploadRepository.fileUploadImageToCloudFlare(cloudKey,multimediaFile!!).run {
                    requestJoinClub(clubId, clubNickName,this)
                }
            }else{
                requestJoinClub(clubId, clubNickName, profileDefaultImage.value)
            }

        }
    }

    private val _joinErrorLiveData =SingleLiveEvent<String>()
    val joinErrorLiveData : LiveData<String> get() = _joinErrorLiveData
    private fun requestJoinClub(clubId: String,clubNickName :String,profileImageId :String?){
        viewModelScope.launch {
            if(!isUser){
                return@launch
            }
            repository.callRequestClubJoin(clubId,integUid!!,clubNickName,accessToken!!,nickNameCheckToken,profileImageId).run {
                if(this.second!=null){
                    _joinErrorLiveData.value=this.second?.code?: ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                if(this.first!=null){
                   clubJoinResultLiveData.value=this.first
                }
            }
        }
    }

    private val _clubDuplicateLiveData = SingleLiveEvent<Boolean>()
    val clubDuplicateLiveData :LiveData<Boolean> get() = _clubDuplicateLiveData
    private var nickNameCheckToken = ""
    fun checkDuplicate(clubId: String, nickName :String){
        viewModelScope.launch {
            repository.checkNicknameDuplicate(clubId,nickName).run {
                _clubDuplicateLiveData.value=this.first!!
                nickNameCheckToken=this.second
            }
        }
    }

    private var _profileDefaultImage = MutableLiveData<String>("")
    val profileDefaultImage :LiveData<String> get() = _profileDefaultImage
    fun settingDefaultProfileImage(imageId : String){
        _profileDefaultImage.value=imageId
    }

    private var multimediaFile : MultipartBody.Part? = null
    fun makeMultiFileAsUri(uri :String, mediaType : MultimediaType, context: Context) : Boolean{
        Uri.parse(uri).asMultipart(
            "file",
            "${mediaType.name}_${Date().time}",
            context.contentResolver
        ).run {
            if(this ==null) {
                multimediaFile=null
                return false
            }
            multimediaFile= this
            return true
        }
    }

}