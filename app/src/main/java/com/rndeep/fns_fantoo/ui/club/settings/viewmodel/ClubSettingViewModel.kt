package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class ClubSettingViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }

    suspend fun getAccessToken(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
    }

    suspend fun getUID(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
    }

}