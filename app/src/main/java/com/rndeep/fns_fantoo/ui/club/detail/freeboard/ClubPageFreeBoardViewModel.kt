package com.rndeep.fns_fantoo.ui.club.detail.freeboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItem
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageRepository
import com.rndeep.fns_fantoo.ui.common.post.CommonPostViewModel
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubPageFreeBoardViewModel @Inject constructor(
    private val repository: ClubPageRepository,
    private val dataStoreRepository: DataStoreRepository,
) : CommonPostViewModel(repository,dataStoreRepository) {

    private val _clubFreeBoardErrorCodeLiveData=SingleLiveEvent<String>()
    val clubFreeBoardErrorCodeLiveData :LiveData<String> get() = _clubFreeBoardErrorCodeLiveData

    private var currentDetailCode :String? = null
    private var currentPostItemUrl :String? =null
    private val _freeBoardCategoryLiveData = MutableLiveData<List<ClubSubCategoryItem>>()
    val freeBoardCategoryLiveData : LiveData<List<ClubSubCategoryItem>> get() = _freeBoardCategoryLiveData
    fun getFreeBoardCategoryList(clubID : String,categoryCode : String,detailCategoryCode :String?){
        viewModelScope.launch {
            repository.fetchDetailFreeBoardCategory(clubID,categoryCode,uId?:"").run {
                if(this.second!=null){
                    _clubFreeBoardErrorCodeLiveData.value=this.second?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                _freeBoardCategoryLiveData.value=this.first?: listOf()
                if(!this.first.isNullOrEmpty()){
                    if(detailCategoryCode!=null || currentDetailCode!=null){
                        for(a in this.first!!.indices){
                            if(this.first!![a].categoryCode == detailCategoryCode ||
                                this.first!![a].categoryCode == currentDetailCode){
                                getClubDetailFreeBoardPostItem(this.first!![a].url)
                                break
                            }
                        }
                        currentDetailCode=detailCategoryCode
                    }else{
                        getClubDetailFreeBoardPostItem(this.first!![0].url)
                        currentDetailCode=this.first!![0].categoryCode
                    }
                }
            }
        }
    }

    var currentNextId:String? = null
    val detailClubFreeBoardPostItems = MutableLiveData<List<BoardPagePosts>>()
    fun getClubDetailFreeBoardPostItem(postUrl : String){
        viewModelScope.launch {
            if(currentNextId == "-1" ) return@launch
            currentPostItemUrl=postUrl
            val size = if((detailClubFreeBoardPostItems.value?.size?:0)>10)detailClubFreeBoardPostItems.value?.size?:10 else 10
            val postResult =repository.getDetailFreeBoardPostData(postUrl,uId?:"",currentNextId,size)
            if(postResult.second!=null){
                _clubFreeBoardErrorCodeLiveData.value=postResult.second?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
            }else{
                detailClubFreeBoardPostItems.value=postResult.first!!
            }
            currentNextId=postResult.third.toString()
        }
    }

    fun addClubFreeBoardPostItem(){
        viewModelScope.launch {
            if(currentNextId == "-1" || currentPostItemUrl ==null) return@launch
            val postResult =repository.getDetailFreeBoardPostData(currentPostItemUrl!!,uId?:"",currentNextId,10)
            if(postResult.second!=null){
                _clubFreeBoardErrorCodeLiveData.value=postResult.second?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
            }else{
                detailClubFreeBoardPostItems.value?.let {
                    detailClubFreeBoardPostItems.value=it+postResult.first!!
                }
            }
            currentNextId=postResult.third.toString()
        }
    }

    fun initNextId(){
        currentNextId=null
    }


}