package com.rndeep.fns_fantoo.ui.menu.editprofile.interest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.UserInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.userinfo.Category
import com.rndeep.fns_fantoo.data.remote.model.userinfo.Interest
import com.rndeep.fns_fantoo.data.remote.model.userinfo.UserInfoType
import com.rndeep.fns_fantoo.repositories.UserInfoRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SelectInterestViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private lateinit var accessToken: String
    private lateinit var integUid: String
    private lateinit var userInfo: UserInfoResponse
    private lateinit var categories: List<Category>

    private var _interestItems = MutableLiveData<List<Category>>()
    val interestItems: LiveData<List<Category>>
        get() = _interestItems

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchInterestCategory()
            fetchUserInfo()
        }
    }

    fun updateCheckedItem(item: Category) {
        categories.find { category ->
            category.code == item.code
        }!!.selectYn = !item.selectYn
        _interestItems.value = categories.map { it.copy() }
        Timber.d("updated item: $item")
    }

    private fun fetchUserInfo() = viewModelScope.launch {
        val response = userInfoRepository.fetchUserInfo(accessToken, IntegUid(integUid))
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                userInfo = response.data
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun fetchInterestCategory() = viewModelScope.launch {
        val response = userInfoRepository.fetchInterestCategory(accessToken, integUid)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                categories = response.data.interestList
                getItems()
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun updateInterest() = viewModelScope.launch {
        userInfo.interestList = getSelectedItem()
        val response = userInfoRepository.updateUserInfoByType(accessToken, userInfo, UserInfoType.INTEREST.type)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("success, interest: ${userInfo.interestList}")
                userInfoRepository.updateInterest(getCodeList(userInfo.interestList).toString())
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun getItems() {
        categories.forEach { category ->
            val index = category.description.indexOf(" ")
            if(index != 0) {
                val desc = category.description.substring(0, index)
                Timber.d("index: $index, desc: $desc")
                category.description = desc
            }
            Timber.d("getItems, category: $category")
        }
        _interestItems.value = categories
    }

    fun checkEnableCount(): Boolean {
        var count = 0
        _interestItems.value?.forEach { category ->
            if(category.selectYn) count++
        }
        Timber.d("count: $count")
        Timber.d("categories: $categories")
        return count <= MAX_INTEREST_COUNT
    }

    private fun getSelectedItem() : List<Interest> {
        val selectedItem = mutableListOf<Interest>()
        _interestItems.value?.forEach { category ->
            if(category.selectYn) {
                selectedItem.add(Interest(category.code))
            }
        }
        Timber.d("getSelectedList: $selectedItem")
        return selectedItem
    }

    private fun getCodeList(list: List<Interest>) : List<String> {
        val codeList = mutableListOf<String>()
        list.forEach {
            codeList.add(it.code)
        }
        Timber.d("codeList: $codeList")
        return codeList
    }

    companion object {
        const val MAX_INTEREST_COUNT = 3
    }
}