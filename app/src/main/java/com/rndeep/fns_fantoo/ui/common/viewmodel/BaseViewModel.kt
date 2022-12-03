package com.rndeep.fns_fantoo.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(private val dataStoreRepository: DataStoreRepository):
    ViewModel(){

    suspend fun getLanguageCode():String?{
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }

    fun setLanguageCode(languageCode: String){
        viewModelScope.launch{
            dataStoreRepository.putString(DataStoreKey.PREF_KEY_LANGUAGE_CODE,languageCode)
        }
    }

}