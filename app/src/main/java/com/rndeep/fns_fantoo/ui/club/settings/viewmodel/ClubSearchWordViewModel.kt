package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubHashTagListResponse
import com.rndeep.fns_fantoo.data.remote.dto.ConfigResponse
import com.rndeep.fns_fantoo.ui.club.settings.data.TagItem
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubSearchWordViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) :
    ViewModel() {

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    var _clubHashTagList = MutableLiveData<List<TagItem>>()
    val clubHashTagList: LiveData<List<TagItem>>
        get() = _clubHashTagList

    fun addSearchWord(tagItem: TagItem): AddResult {
        if (_clubHashTagList.value != null) {
            val tagItemsList = _clubHashTagList.value
            if (tagItemsList != null) {
                if (tagItemsList.size >= ConstVariable.ClubDef.CLUB_SEARCH_WORD_MAX_ITEM_COUNT) {
                    return AddResult.AddResultMaxItem
                }
                for (childTagItem in tagItemsList) {
                    if (childTagItem.name == tagItem.name) {
                        return AddResult.AddResultDuplicateItem
                    }
                }
                val list = tagItemsList.toMutableList()
                list.add(tagItem)
                _clubHashTagList.value = list
                return AddResult.AddResultOk
            }
        }
        _clubHashTagList.value = listOf(tagItem)
        return AddResult.AddResultOk
    }

    fun removeSearchWord(tagItem: TagItem) = viewModelScope.launch {
        if (_clubHashTagList.value != null) {
            var tagItemsList = _clubHashTagList.value!!.toMutableList()
            tagItemsList.remove(tagItem)
            _clubHashTagList.value = tagItemsList
        }
    }

    private var _clubHashTagListLiveData = MutableLiveData<ClubHashTagListResponse>()
    val clubHashTagListLiveData: LiveData<ClubHashTagListResponse>
        get() = _clubHashTagListLiveData

    fun getSearchWordList(clubId: String, uid: String) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = clubSettingRepository.getHashTagList(clubId, uid)
        loadingStatusLiveData.value = false
        when (response) {
            is ResultWrapper.Success -> {
                _clubHashTagListLiveData.value =
                    ClubHashTagListResponse(
                        ConstVariable.RESULT_SUCCESS_CODE,
                        "",
                        response.data,
                        null
                    )
            }
            is ResultWrapper.GenericError -> {
                _clubHashTagListLiveData.value =
                    ClubHashTagListResponse(
                        response.code!!,
                        response.message!!,
                        null,
                        response.errorData
                    )
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _clubHashTagListSaveResultLiveData = MutableLiveData<ClubHashTagListResponse>()
    val clubHashTagListSaveResultLiveData: LiveData<ClubHashTagListResponse>
        get() = _clubHashTagListSaveResultLiveData

    fun saveSearchWordList(clubId: String, hashMap: HashMap<String, Any>) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response = clubSettingRepository.saveHashTagList(clubId, hashMap)
        loadingStatusLiveData.value = false
        when (response) {
            is ResultWrapper.Success -> {
                _clubHashTagListSaveResultLiveData.value = ClubHashTagListResponse(
                    ConstVariable.RESULT_SUCCESS_CODE,
                    "",
                    response.data,
                    null
                )
            }
            is ResultWrapper.GenericError -> {
                _clubHashTagListSaveResultLiveData.value = ClubHashTagListResponse(
                    response.code!!,
                    response.message!!,
                    null,
                    response.errorData
                )
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }
}

enum class AddResult {
    AddResultOk,
    AddResultMaxItem,
    AddResultDuplicateItem
}