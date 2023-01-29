package com.rndeep.fns_fantoo.ui.home.tabhome

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.ui.common.post.CommonPostViewModel
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendSelect
import com.rndeep.fns_fantoo.data.local.model.BannerItem
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val dataStoreRepository: DataStoreRepository,
) : CommonPostViewModel(homeRepository, dataStoreRepository) {

    private val _errorCodeLiveData = SingleLiveEvent<Int>()
    val errorCodeLiveData: LiveData<Int> get() = _errorCodeLiveData

    private val _loadingStateLiveData = MutableLiveData<Boolean>()
    val loadingStateLiveData: LiveData<Boolean> get() = _loadingStateLiveData
    fun changeLoadingState(loadingState: Boolean) {
        _loadingStateLiveData.value = loadingState
    }


    //====================HOMETAB========================
    //Home 카테고리 전체 리스트 Db
    val homeItemLiveData = MutableLiveData<List<BoardPagePosts>>()

    //Home 카테고리 Banner 정보를 가지고 있는 DB
    val bannerItemLiveData = SingleLiveEvent<List<BannerItem>>()

    //Home 카테고리 추천 클럽 정보를 가지고 있는 DB
    val homeRecommendClubLiveData = SingleLiveEvent<List<CommonRecommendSelect>>()

    //처음 home 데이터를 받는 함수
    fun getInitHomeData(context: Context) {
        viewModelScope.launch {
            //home 의 bannerData call
            bannerItemLiveData.value = homeRepository.getBannerItem()
            //추천
            homeRecommendClubLiveData.value = homeRepository.getRecommendClub(
                ConstVariable.DB_HOMECATEGORY,
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
            )
            //home 의 postData call
            val data = homeRepository.callInitHomeData(0, context)
            if (data.second != null) {
                _errorCodeLiveData.value = data.second!!.code!!.toInt()
            }
            homeItemLiveData.value = data.first!!
        }
    }

    fun getGuestData() {
        viewModelScope.launch {
            //home 의 bannerData call
            bannerItemLiveData.value = homeRepository.getBannerItem()
            homeItemLiveData.value = homeRepository.getGuestData()
        }
    }

    val myProfile = homeRepository.getMyProfile().asLiveData()

    var isCheckOrNeedCheck = true
    fun isCheckProfile() {
        viewModelScope.launch {
            if (!isUser()) {
                return@launch
            }
            isCheckOrNeedCheck = false
            homeRepository.getUserInfoData(accessToken!!, uId!!)
        }
    }

    fun getEditProfileView() {
        viewModelScope.launch {
            bannerItemLiveData.value = homeRepository.getBannerItem()
            homeRepository.getEditProfileHomeData().let {
                homeItemLiveData.value = it
            }
//            homeItemLiveData.value= homeRepository.getEditProfileHomeData()
        }
    }

    fun getAddHomeData(pageNum: Int, context: Context) {
        viewModelScope.launch {
            val data = homeRepository.callInitHomeData((pageNum), context)
            if (data.second != null) {
                _errorCodeLiveData.value = data.second!!.code!!.toInt()
            }
            homeItemLiveData.value = data.first!!
        }
    }

    fun getAllItem() {
        viewModelScope.launch {
            homeItemLiveData.value = homeRepository.getAllHomeItem()
        }
    }


}