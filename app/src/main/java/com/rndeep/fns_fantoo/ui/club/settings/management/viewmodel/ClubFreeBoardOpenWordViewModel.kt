package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItemResponse
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.club.settings.data.TagItem
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.AddResult
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ClubFreeBoardOpenWordViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository,
private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    var _freeBoardOpenWordList = MutableLiveData<List<TagItem>>()
    val freeBoardOpenWordList : LiveData<List<TagItem>>
        get() = _freeBoardOpenWordList

    fun addOpenWord(tagItem: TagItem): AddResult {
        if(_freeBoardOpenWordList.value != null){
            val tagItemsList = _freeBoardOpenWordList.value
            if (tagItemsList != null) {
                if(tagItemsList.size >= ConstVariable.ClubDef.CLUB_FREEBOARD_OPEN_WORD_MAX_ITEM_COUNT){
                    return AddResult.AddResultMaxItem
                }
                for(childTagItem in tagItemsList){
                    if(childTagItem.name == tagItem.name){
                        return AddResult.AddResultDuplicateItem
                    }
                }
                val list = tagItemsList.toMutableList()
                list.add(tagItem)
                _freeBoardOpenWordList.value = list
                return AddResult.AddResultOk
            }
        }
        _freeBoardOpenWordList.value = listOf(tagItem)
        return AddResult.AddResultOk
    }

    fun removeOpenWord(tagItem: TagItem) = viewModelScope.launch {
        if(_freeBoardOpenWordList.value != null){
            var tagItemsList = _freeBoardOpenWordList.value!!.toMutableList()
            tagItemsList.remove(tagItem)
            _freeBoardOpenWordList.value = tagItemsList
        }
    }

    fun getOpenWordList() = viewModelScope.launch {
        /*val list = listOf(
            TagItem(true, "뉴스"),
                    TagItem(true, "잡담"),
                    TagItem(true, "질문"),
                   TagItem(true, "기타")
        )
        _freeBoardOpenWordList.value = list*/
    }

    private var _openWordSaveResultLiveData = SingleLiveEvent<ClubCategoryItemResponse>()
    val openWordSaveResultLiveData : SingleLiveEvent<ClubCategoryItemResponse>
    get() = _openWordSaveResultLiveData

    fun saveOpenWordList(clubId:String, categoryCode:String, requestData: HashMap<String, Any>) = viewModelScope.launch {
        Timber.d("saveOpenWordList $requestData")
        val response = clubSettingRepository.saveFreeboardOpenWord(clubId = clubId, categoryCode = categoryCode, requestData = requestData)
        when(response){
            is ResultWrapper.Success ->{
                _openWordSaveResultLiveData.value = ClubCategoryItemResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError ->{
                _openWordSaveResultLiveData.value = ClubCategoryItemResponse(response.code!!, response.message!!, null, response.errorData)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }
}