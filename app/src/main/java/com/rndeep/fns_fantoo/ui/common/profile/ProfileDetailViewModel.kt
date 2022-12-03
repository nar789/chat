package com.rndeep.fns_fantoo.ui.common.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.club.post.DetailPostRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val postRepository: DetailPostRepository
) : ViewModel() {

    var isLoginUser :Boolean = false
    private var uId :String? =null
    private var accessToken : String? =null
    var isUserBlock = false

    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isLoginUser=it
                uId=dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
                accessToken=dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
            }
        }
    }

    private val _errorDataLiveData = SingleLiveEvent<String>()
    val errorDataLiveData :LiveData<String> get() = _errorDataLiveData

    private val _userNickNameLiveData =MutableLiveData<String>()
    val userNickNameLiveData :LiveData<String> get() = _userNickNameLiveData
    fun setUserNickName(nickName :String){
        _userNickNameLiveData.value=nickName
    }

    private val _userPhotoLiveData = MutableLiveData<String>()
    val userPhotoLiveData :LiveData<String> get() = _userPhotoLiveData
    fun setUserPhoto(userPhoto : String){
        _userPhotoLiveData.value=userPhoto
    }


    fun patchClubMemberBlock(clubId : String,memberId : Int){
        viewModelScope.launch {
            if(!isLoginUser){
                _errorDataLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            postRepository.patchClubMemberBlock(clubId,memberId,uId!!).run {
                if(this==ConstVariable.RESULT_SUCCESS_CODE){
                    isUserBlock = !isUserBlock
                }else{
                    _errorDataLiveData.value=this
                }
            }
        }
    }

    fun patchCommunityMemberBlock(targetUid:String,isBlock :Boolean){
        viewModelScope.launch {
            if(!isLoginUser){
                _errorDataLiveData.value=ConstVariable.ERROR_NOT_MEMBER
                return@launch
            }
            postRepository.callBlockCommunityAccount(accessToken!!,uId!!,targetUid,isBlock).run {
                if(this== ConstVariable.RESULT_SUCCESS_CODE){
                    isUserBlock = !isUserBlock
                }else{
                    _errorDataLiveData.value=this
                }
            }
        }
    }

}