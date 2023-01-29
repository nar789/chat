package com.rndeep.fns_fantoo.ui.home.tabpopular

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.ui.common.post.CommonPostViewModel
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.utils.SizeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val popularRepository: PopularRepository,
    private val dataStoreRepository: DataStoreRepository
) : CommonPostViewModel(popularRepository, dataStoreRepository) {

    private val _errorDataLiveData = SingleLiveEvent<Int>()
    val errorDataLiveData: LiveData<Int> get() = _errorDataLiveData

    private val _loadingStateLiveData = MutableLiveData<Boolean>()
    val loadingStateLiveData: LiveData<Boolean> get() = _loadingStateLiveData
    fun changeLoadingState(loadingState: Boolean) {
        _loadingStateLiveData.value=loadingState
    }

    // =======================PopularTab=======================================
    val popularItemLiveData = MutableLiveData<List<BoardPagePosts>>()

    //상단 트랜드 태그 아이템
    val trendItemListData = SingleLiveEvent<List<TrendTagItem>>()

    //큐레이션 아이템
    val popularCurationLiveData = SingleLiveEvent<List<CurationDataItem>>()

    // 추천 클럽
    val popularRecommendClubLiveData = SingleLiveEvent<List<CommonRecommendSelect>>()

    var selectOtherLang :String =""

    //처음 populardata 를 받는 함수
    fun getInitPopularData(context: Context) {
        viewModelScope.launch {
            //popular 의 trenddata call
            trendItemListData.value = popularRepository.getTrendItem()
            //popular 의 curation data call
            popularCurationLiveData.value = popularRepository.getCurationData()
            //porpular 의 추천 클럽
            popularRecommendClubLiveData.value =
                popularRepository.getRecommendClub(ConstVariable.DB_POPULARCATEGORY, dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID))
            //popular 의 postData call
            val data = popularRepository.getPopularData(0, context)
            if (data.second != null) {
                _errorDataLiveData.value = data.second!!.code!!.toInt()
            }
            popularItemLiveData.value = data.first!!
        }
    }


    fun getAddPopularData(pageNum: Int, context: Context) {
        viewModelScope.launch {
            val data = popularRepository.getPopularData(pageNum, context)
            if (data.second != null) {
                _errorDataLiveData.value = data.second!!.code!!.toInt()
            }
            popularItemLiveData.value = data.first!!
        }
    }

    fun getAllItem() {
        viewModelScope.launch {
            popularItemLiveData.value = popularRepository.getAllPopularItem()
        }
    }

    fun transFormTrendData(context: Context) {
        SizeUtils.getDeviceSize(context).width
    }


    private var filterSelectPos = 0
    fun getFilterItem(resources : Resources) =  arrayListOf(
            BottomSheetItem(
                null,
                resources.getString(R.string.en_global),
                null,
                filterSelectPos==0
            ),
            BottomSheetItem(
                null,
                resources.getString(R.string.n_setting_to_my_language),
                null,
                filterSelectPos==1
            ),
            BottomSheetItem(
                null,
                "${resources.getString(R.string.d_other_language_select)} ${selectOtherLang}",
                null,
                filterSelectPos==2
            )
        )


    fun setFilterSelectPos(selectPos :Int){
        filterSelectPos=selectPos
    }

}