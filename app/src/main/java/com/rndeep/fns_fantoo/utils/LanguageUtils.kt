package com.rndeep.fns_fantoo.utils

import android.content.Context
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Country

class LanguageUtils {

    companion object{
        const val PORTUGUESE = "pt"
        const val INDONESIAN = "in"
        const val INDONESIAN_OPT = "id" //서버 & https://developers.google.com/interactive-media-ads/docs/sdks/android/client-side/localization
        const val VIETNAMESE = "vi"
        const val THAI = "th"
        const val ARABIC = "ar" //반전 UI지원하지 않고 언어만 표시?
        const val KOREAN = "ko"
        const val CHINESE_SIMPLIFIED = "zh_cn"
        const val CHINESE_TRADITIONAL = "zh_tw"
        const val JAPANESE = "ja"
        const val ENGLISH = "en"
        const val SPANISH = "es"
        const val FRENCH = "fr"
        const val POLISH = "pl"
        const val ITALIAN = "it"
        const val RUSSIAN = "ru"
        const val GERMAN = "de"
        const val HINDI = "hi"

        fun getLangName(context: Context, langCode:String):String{
            var langName:String = ""
            when(langCode){
                PORTUGUESE ->
                    langName = context.getString(R.string.lang_port)
                INDONESIAN_OPT ->
                    langName = context.getString(R.string.lang_ind)
                VIETNAMESE ->
                    langName = context.getString(R.string.lang_viet)
                THAI ->
                    langName = context.getString(R.string.lang_thai)
                ARABIC ->
                    langName = context.getString(R.string.lang_ar)
                KOREAN ->
                    langName = context.getString(R.string.lang_kr)
                JAPANESE ->
                    langName = context.getString(R.string.lang_jp)
                ENGLISH ->
                    langName = context.getString(R.string.lang_en)
                SPANISH ->
                    langName = context.getString(R.string.lang_es)
                FRENCH ->
                    langName = context.getString(R.string.lang_fr)
                POLISH ->
                    langName = context.getString(R.string.lang_pol)
                ITALIAN ->
                    langName = context.getString(R.string.lang_it)
                RUSSIAN ->
                    langName = context.getString(R.string.lang_ru)
                GERMAN ->
                    langName = context.getString(R.string.lang_de)
                HINDI ->
                    langName = context.getString(R.string.lang_hi)
                CHINESE_SIMPLIFIED ->
                    langName = context.getString(R.string.lang_cn)
                CHINESE_TRADITIONAL ->
                    langName = context.getString(R.string.lang_tr)
            }
            return langName
        }

        /*
        fun getSupportLanguages(context:Context): ArrayList<CountryWrapperItem> {
            //임시 데이터
            return arrayListOf<CountryWrapperItem>(
                CountryWrapperItem(
                    Country(context.getString(R.string.lang_port), context.getString(R.string.lang_port), PORTUGUESE, "",
            "", "", -1)
                ),
                CountryWrapperItem(Country(context.getString(R.string.lang_ind), context.getString(R.string.lang_ind), INDONESIAN, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_viet), context.getString(R.string.lang_viet), VIETNAMESE, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_thai), context.getString(R.string.lang_thai), THAI, "",
                    "", "", -1)),
                //LanguageItem(context.getString(R.string.lang_ar), ARABIC),  추후 지원
                CountryWrapperItem(Country(context.getString(R.string.lang_kr), context.getString(R.string.lang_kr), KOREAN, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_cn), context.getString(R.string.lang_cn), CHINESE_SIMPLIFIED, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_tr), context.getString(R.string.lang_tr), CHINESE_TRADITIONAL, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_jp), context.getString(R.string.lang_jp), JAPANESE, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_en), context.getString(R.string.lang_en), ENGLISH, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_es), context.getString(R.string.lang_es), SPANISH, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_fr), context.getString(R.string.lang_fr), FRENCH, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_pol), context.getString(R.string.lang_pol), POLISH, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_it), context.getString(R.string.lang_it), ITALIAN, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_ru), context.getString(R.string.lang_ru), RUSSIAN, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_de), context.getString(R.string.lang_de), GERMAN, "",
                    "", "", -1)),
                CountryWrapperItem(Country(context.getString(R.string.lang_hi), context.getString(R.string.lang_hi), HINDI, "",
                    "", "", -1)),
            )
        }
        */
    }
}