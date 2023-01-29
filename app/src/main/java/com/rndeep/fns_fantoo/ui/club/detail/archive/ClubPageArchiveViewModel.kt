package com.rndeep.fns_fantoo.ui.club.detail.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItem
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageRepository
import com.rndeep.fns_fantoo.ui.common.post.CommonPostViewModel
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubPageArchiveViewModel @Inject constructor(
    private val clubPageRepository: ClubPageRepository,
    private val dataStoreRepository: DataStoreRepository
) : CommonPostViewModel(clubPageRepository,dataStoreRepository) {

    var currentPostUrl :String? =null
    val clubArchivePostLiveData = SingleLiveEvent<List<ClubSubCategoryItem>>()
    private val _archiveErrorData = SingleLiveEvent<String>()
    val archiveErrorData : LiveData<String> get() = _archiveErrorData
    fun getArchiveBoardList(clubId :String,categoryCode: String) {
        viewModelScope.launch {
            clubPageRepository.fetchDetailFreeBoardCategory(
                clubId,
                categoryCode,
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?:"guest"
            ).run {
                if(this.second!=null){
                    _archiveErrorData.value=this.second?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }else{
                    clubArchivePostLiveData.value=this.first!!
                }
            }
        }
    }

    val archiveImageLiveData = SingleLiveEvent<List<ClubPostData>>()
    private var archvieNextId :String? = null
    private var archivePostSize =0
    fun getArchiveImageList(url: String) {
        viewModelScope.launch {
            val size = if(archivePostSize<10)10 else archivePostSize
            clubPageRepository.getArchiveImageList(
                url,
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?:"guest",
                archvieNextId,
                size,
                null
            ).run {
                if(this.third!=null){
                    _archiveErrorData.value=this.third?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }else{
                    archiveImageLiveData.value=this.first!!
                }
                archvieNextId=this.second.toString()
                archivePostSize=archiveImageLiveData.value?.size?:0
            }

        }
    }
    fun initPostItemForInfo(){
        archvieNextId=null
        archivePostSize=0
    }

    val archiveCommonLiveData = SingleLiveEvent<List<BoardPagePosts>>()
    fun getArchiveCommonList(postUrl: String) {
        viewModelScope.launch {
            val size = if(archivePostSize<10)10 else archivePostSize
            clubPageRepository.getArchiveCommonData(
                postUrl,
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)?:"guest",
                archvieNextId,
                size,
                null
            ).run {
                if(this.third!=null){
                    _archiveErrorData.value=this.third?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }else{
                    archiveCommonLiveData.value =this.first!!
                }
                archvieNextId=this.second.toString()
                archivePostSize=archiveCommonLiveData.value?.size?:0
            }
        }
    }

}