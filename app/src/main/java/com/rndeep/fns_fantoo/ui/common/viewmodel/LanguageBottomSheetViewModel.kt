package com.rndeep.fns_fantoo.ui.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.data.remote.model.Language
import com.rndeep.fns_fantoo.ui.common.LanguageRepository
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LanguageBottomSheetViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val languageRepository: LanguageRepository
) : ViewModel() {

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }

    private var _countryAllList = MutableLiveData<List<Country>?>()
    val countryAllList: MutableLiveData<List<Country>?>
        get() = _countryAllList

    suspend fun getCountryAll() = viewModelScope.launch {
        val response = languageRepository.getCountryAll()
        Timber.d("getCountryAll $response")
        when (response) {
            is ResultWrapper.Success -> {
                _countryAllList.value = response.data
            }
            else -> {}
        }
    }

    private var _countryAllEntityList =
        MutableLiveData<List<com.rndeep.fns_fantoo.data.local.model.Country>>()
    val countryAllEntityList: MutableLiveData<List<com.rndeep.fns_fantoo.data.local.model.Country>>
        get() = _countryAllEntityList

    suspend fun getCountryAllFromDB() {
        _countryAllEntityList.value = languageRepository.getCountryAllDB()
    }

    suspend fun addAllCountry(countryList: List<com.rndeep.fns_fantoo.data.local.model.Country>) {
        languageRepository.addAllCountryDB(countryList)
    }


    private var _languageList = MutableLiveData<List<Language>?>()
    val languageList: MutableLiveData<List<Language>?>
        get() = _languageList

    suspend fun getSupportLanguageAll() = viewModelScope.launch {
        val response = languageRepository.getSupportLanguageAll()
        Timber.d("getSupportLanguageAll $response")
        try {
            when (response) {
                is ResultWrapper.Success -> {
                    _languageList.value = response.data
                }
                else -> {}
            }
        } catch (e: Exception) {
            Timber.e("getSupportLanguageAll err : ${e.message}")
        }
    }

    private var _languageEntityList =
        MutableLiveData<List<com.rndeep.fns_fantoo.data.local.model.Language>>()
    val languageEntityList: MutableLiveData<List<com.rndeep.fns_fantoo.data.local.model.Language>>
        get() = _languageEntityList

    suspend fun getSupportLanguageAllFromDB() {
        _languageEntityList.value = languageRepository.getLanguageAllDB()
    }

    suspend fun addAllLanguageDB(languageList: List<com.rndeep.fns_fantoo.data.local.model.Language>) {
        languageRepository.addAllLanguageToDB(languageList)
    }

}