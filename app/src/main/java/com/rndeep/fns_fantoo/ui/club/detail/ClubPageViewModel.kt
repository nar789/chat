package com.rndeep.fns_fantoo.ui.club.detail

import android.graphics.Bitmap
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.rndeep.fns_fantoo.data.remote.model.ClubBasicInfo
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItem
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubDelegatingInfoDto
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubPageViewModel @Inject constructor(
    private val repository: ClubPageRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var accessToken : String? =null
    private var uId :String? =null
    private var isUser :Boolean =false

    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isUser=it
                if(it){
                    accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
                    uId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
                }
            }
        }
    }

    fun isLogin()=isUser

    private val _clubDetailErrorLivedata =SingleLiveEvent<String>()
    val clubDetailErrorLiveData : LiveData<String> get() = _clubDetailErrorLivedata
    //클럽 상단 정보 데이터
    private val _clubTopInfoLiveData = MutableLiveData<ClubBasicInfo?>()
    val clubTopInfoLiveData :LiveData<ClubBasicInfo?> get() = _clubTopInfoLiveData
    private val _clubDelegateDialog = SingleLiveEvent<ClubDelegatingInfoDto>()
    val clubDelegateDialog :LiveData<ClubDelegatingInfoDto> get() = _clubDelegateDialog

    fun getClubTopInfoItems(clubId :String) {
        viewModelScope.launch {
            val clubItem = repository.getClubBasicInfo(clubId,uId)
            _clubTopInfoLiveData.value=clubItem.first
            if(clubItem.second!=null){

            }
            clubItem.third?.let {
                _clubDelegateDialog.value=it
            }
        }
    }

    private val _clubFavoriteChangeLiveData = SingleLiveEvent<Boolean>()
    val clubFavoriteChangeLiveData :LiveData<Boolean> get() = _clubFavoriteChangeLiveData
    fun patchClubFavoriteState(clubId: String){
        viewModelScope.launch {
            if(!isUser){
                _clubDetailErrorLivedata.value=ConstVariable.ERROR_PLEASE_LATER_LOGIN
                return@launch
            }
            _clubFavoriteChangeLiveData.value=repository.patchClubFavorite(clubId,uId!!)
        }
    }

    //클럽 카테고리
    private val _clubCategoryListLiveData = SingleLiveEvent<List<ClubCategoryItem>>()
    val clubCategoryListLiveData :LiveData<List<ClubCategoryItem>> get() = _clubCategoryListLiveData
    fun getClubCategory(clubId:String){
        viewModelScope.launch {
            repository.getClubCategory(clubId, uId?:"guest").run {
                if(this.isEmpty()){
                    _clubDetailErrorLivedata.value=ConstVariable.ERROR_NO_CATEGORY_CLUB
                }else{
                    _clubCategoryListLiveData.value=this
                }
            }
        }
    }

    val clubImageRGBLevel = MutableLiveData<Double>()
    fun getImageColorLevel(resource: Bitmap) {
        viewModelScope.launch {
            Palette.from(resource).generate { palette ->
                palette?.let { it ->
                    //원하는 swatch
                    val dominantSwatch: Palette.Swatch? = it.dominantSwatch
                    //swatch에서 rbg 값 뽑기
                    val dominantRgb = dominantSwatch?.rgb
                    dominantRgb?.let { rgb ->
                        //값이 0.5. 보다 작으면 어두운거
                        clubImageRGBLevel.value = ColorUtils.calculateLuminance(rgb)
                    }
                }
            }
        }
    }

    var delegateCompleteDate :String? =null
    private val _delegateState = SingleLiveEvent<Boolean>()
    val delegateState : LiveData<Boolean> get() = _delegateState
    fun choiceClubMasterDelegate(clubId :String, isAccept:Boolean){
        viewModelScope.launch {
            repository.patchClubMasterDelegateState(clubId,uId?:"guest",isAccept).run {
                if(this == ConstVariable.RESULT_SUCCESS_CODE){
                    _delegateState.value=isAccept
                }else{

                }
            }
        }
    }

}