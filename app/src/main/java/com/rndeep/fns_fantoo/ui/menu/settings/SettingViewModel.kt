package com.rndeep.fns_fantoo.ui.menu.settings

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.BuildConfig
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val menuRepository: MenuRepository
) : ViewModel() {


    private var _settingsMenu = MutableLiveData<List<MenuItem>>()
    val settingsMenu: LiveData<List<MenuItem>>
        get() = _settingsMenu

    private var _pushNotificationMenu = MutableLiveData<List<MenuItem>>()
    val pushNotificationMenu: LiveData<List<MenuItem>>
        get() = _pushNotificationMenu

    private val accountInfo = MenuItem.Item(
        MenuItemType.ACCOUNT_INFO,
        null,
        IconType.IMAGE
    )

    private val serviceSettingTitle = MenuItem.Title(application.applicationContext.getString(R.string.s_setting_service))
    private val translateLanguage = MenuItem.Item(
        MenuItemType.SELECT_TRANSLATE_LANGUAGE,
        null,
        IconType.NONE
    )
    private val pushNotificationSet = MenuItem.Item(
        MenuItemType.PUSH_NOTIFICATION_SETTING,
        null,
        IconType.IMAGE
    )
    private val marketingAgree = MenuItem.SwitchItem(
        MenuItemType.MARKETING_AGREE,
        false
    )

    private val customerSupportTitle = MenuItem.Title(application.applicationContext.getString(R.string.g_customer_support))
    private val currentVersion = MenuItem.VersionItem(
        MenuItemType.CURRENT_VERSION,
        BuildConfig.VERSION_NAME,
        false
    )
    private val notice = MenuItem.Item(
        MenuItemType.NOTICE,
        null,
        IconType.IMAGE
    )
    private val contactEmail = MenuItem.Item(
        MenuItemType.CONTACT_EMAIL,
        null,
        IconType.IMAGE
    )

    private val serviceTermsTitle = MenuItem.Title(application.applicationContext.getString(R.string.s_term_service))
    private val termsOfService = MenuItem.Item(
        MenuItemType.TERMS_OF_SERVICE,
        null,
        IconType.IMAGE
    )
    private val privacyPolicy = MenuItem.Item(
        MenuItemType.PRIVACY_POLICY,
        null,
        IconType.IMAGE
    )
    private val youthProtectionPolicy = MenuItem.Item(
        MenuItemType.YOUTH_PROTECTION_POLICY,
        null,
        IconType.IMAGE
    )

    private val settingMenuItems = listOf(
        accountInfo,
        serviceSettingTitle, translateLanguage, pushNotificationSet, marketingAgree,
        customerSupportTitle, currentVersion, notice, contactEmail,
        serviceTermsTitle, termsOfService, privacyPolicy, youthProtectionPolicy
    )

    //
    private val communityPushTitle = MenuItem.Title(application.applicationContext.getString(R.string.k_community))
    private val communityPush = MenuItem.SwitchItem(
        MenuItemType.PUSH_COMMUNITY,
        true
    )

    private val clubPushTitle = MenuItem.Title(application.applicationContext.getString(R.string.k_club))
    private val clubPush1 = MenuItem.SwitchItem(
        MenuItemType.PUSH_CLUB,
        false
    )
    private val clubPush2 = MenuItem.SwitchItem(
        MenuItemType.PUSH_CLUB,
        false
    )

    private val pushNotificationSettingItems = listOf(
        communityPushTitle, communityPush,
        clubPushTitle, clubPush1, clubPush2
    )

    init {
        viewModelScope.launch {
            setSettingMenuItem()
        }
    }

    private suspend fun setSettingMenuItem() {
        translateLanguage.value = getLanguageCode()?.let { getLanguageTitleByCode(it) }
        _settingsMenu.postValue(settingMenuItems)
        _pushNotificationMenu.postValue(pushNotificationSettingItems)
    }

    private fun updateLanguage(language: String) {
        Timber.d("language : $language")
        translateLanguage.value = getLanguageTitleByCode(language)
        _settingsMenu.value = settingMenuItems
    }

    fun startSubscribeAds(isSubscribed: Boolean) {
        Timber.d("start Logout")
//        menuRepository.startSubscribeAds(isSubscribed)
    }

    suspend fun getLanguageCode(): String? {
        return dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
    }

    fun setLanguageCode(languageCode: String) = viewModelScope.launch {
        dataStoreRepository.putString(DataStoreKey.PREF_KEY_LANGUAGE_CODE, languageCode)
        updateLanguage(languageCode)
    }




    private fun getLanguageTitleByCode(languageCode: String) = when (languageCode) {
        "pt" -> application.applicationContext.getString(R.string.lang_port)
        "id" -> application.applicationContext.getString(R.string.lang_ind)
        "vi" -> application.applicationContext.getString(R.string.lang_viet)
        "th" -> application.applicationContext.getString(R.string.lang_thai)
        "ar" -> application.applicationContext.getString(R.string.lang_ar)
        "ko" -> application.applicationContext.getString(R.string.lang_kr)
        "zh_cn" -> application.applicationContext.getString(R.string.lang_cn)
        "zh_tw" -> application.applicationContext.getString(R.string.lang_tr)
        "ja" -> application.applicationContext.getString(R.string.lang_jp)
        "en" -> application.applicationContext.getString(R.string.lang_en)
        "es" -> application.applicationContext.getString(R.string.lang_es)
        "fr" -> application.applicationContext.getString(R.string.lang_fr)
        "pl" -> application.applicationContext.getString(R.string.lang_pol)
        "it" -> application.applicationContext.getString(R.string.lang_it)
        "ru" -> application.applicationContext.getString(R.string.lang_ru)
        "de" -> application.applicationContext.getString(R.string.lang_de)
        "hi" -> application.applicationContext.getString(R.string.lang_hi)
        else -> "Not Supported"
    }
}