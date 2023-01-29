package com.rndeep.fns_fantoo.ui.common

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rndeep.fns_fantoo.ui.common.viewmodel.BaseViewModel
import com.rndeep.fns_fantoo.utils.LanguageUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
abstract class LocaleSetFragment : Fragment() {
    private val baseViewModel: BaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(requireActivity().baseContext)
    }

    private fun setLocale(context: Context) {
        val language = getLanguage()
        val config = context.resources?.configuration
        val sysLocale: Locale
        if (config != null) {
            sysLocale = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                AppContextWrapper.getSystemLocale(config)
            } else {
                AppContextWrapper.getSystemLocaleLegacy(config)
            }
            if (language.isNotEmpty() && !sysLocale.language.equals(language)) {
                val locale = Locale(language)
                Locale.setDefault(locale)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    AppContextWrapper.setSystemLocale(config, locale)
                } else {
                    AppContextWrapper.setSystemLocaleLegacy(config, locale)
                }
            }

            @Suppress("DEPRECATION")
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    private fun getLanguage(): String {
        var language :String? = ""
        lifecycleScope.launch {
            language = baseViewModel.getLanguageCode()
        }
        val lang: Locale
        val isEmptyLang = language.isNullOrEmpty()
        if (isEmptyLang) {
            language = Locale.getDefault().language
        }
        when (language) {
            LanguageUtils.ARABIC -> {
                lang = Locale("ar")
            }
            LanguageUtils.CHINESE_SIMPLIFIED -> {
                lang = Locale.SIMPLIFIED_CHINESE
            }
            LanguageUtils.CHINESE_TRADITIONAL -> {
                lang = Locale.TRADITIONAL_CHINESE
            }
            //case, china system lang
            "zh" -> {
                val zhStr: String = Locale.getDefault().toString()
                lang = if (zhStr.contains("TW") || zhStr.contains("HK")) {
                    Locale.TRADITIONAL_CHINESE
                } else {
                    Locale.SIMPLIFIED_CHINESE
                }
            }
            LanguageUtils.ENGLISH -> {
                lang = Locale.ENGLISH
            }
            LanguageUtils.FRENCH -> {
                lang = Locale.FRANCE
            }
            LanguageUtils.GERMAN -> {
                lang = Locale.GERMAN
            }
            LanguageUtils.HINDI -> {
                lang = Locale("hi")
            }
            LanguageUtils.INDONESIAN, LanguageUtils.INDONESIAN_OPT -> {//system code => in
                lang = Locale("in", "ID")
            }
            LanguageUtils.ITALIAN -> {
                lang = Locale.ITALIAN
            }
            LanguageUtils.JAPANESE -> {
                lang = Locale.JAPANESE
            }
            LanguageUtils.KOREAN -> {
                lang = Locale.KOREA
            }
            LanguageUtils.POLISH -> {
                lang = Locale("pl")
            }
            LanguageUtils.PORTUGUESE -> {
                lang = Locale("pt")
            }
            LanguageUtils.RUSSIAN -> {
                lang = Locale("ru", "RU")
            }
            LanguageUtils.SPANISH -> {
                lang = Locale("es", "ES")
            }
            LanguageUtils.THAI -> {
                lang = Locale("th")
            }
            LanguageUtils.VIETNAMESE -> {
                lang = Locale("vi")
            }
            else -> {
                lang = Locale.ENGLISH
                language = LanguageUtils.ENGLISH
            }
        }
        if (isEmptyLang) {
            setPreferenceLanguage(lang)
        }
        return language?:LanguageUtils.ENGLISH

    }

    private fun setPreferenceLanguage(locale: Locale) {
        val langStr = locale.toString()
        var lang = "en"
        when (langStr) {
            "ko_KR", "ko" -> {
                lang = LanguageUtils.KOREAN
            }
            "en" -> {
                lang = LanguageUtils.ENGLISH
            }
            "ja" -> {
                lang = LanguageUtils.JAPANESE
            }
            "zh_CN" -> {
                lang = LanguageUtils.CHINESE_SIMPLIFIED
            }
            //"zh_TW_#Hant" ,"zh_HK_#Hant"
            "zh_TW" -> {
                lang = LanguageUtils.CHINESE_TRADITIONAL
            }
            "in_ID", "in" -> {
                lang = LanguageUtils.INDONESIAN
            }
            "es_ES" -> {
                lang = LanguageUtils.SPANISH
            }
            "fr_FR" -> {
                lang = LanguageUtils.FRENCH
            }
            "ru_RU", "ru" -> {
                lang = LanguageUtils.RUSSIAN
            }
            "de" -> {
                lang = LanguageUtils.GERMAN
            }
            "pt" -> {
                lang = LanguageUtils.PORTUGUESE
            }
            "vi" -> {
                lang = LanguageUtils.VIETNAMESE
            }
            "th" -> {
                lang = LanguageUtils.THAI
            }
            "ar" -> {
                lang = LanguageUtils.ARABIC
            }
            "pl" -> {
                lang = LanguageUtils.POLISH
            }
            "it" -> {
                lang = LanguageUtils.ITALIAN
            }
            "hi" -> {
                lang = LanguageUtils.HINDI
            }
        }
        lifecycleScope.launch {
            baseViewModel.setLanguageCode(lang)
        }
    }
}