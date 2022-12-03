package com.rndeep.fns_fantoo.ui.common

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import java.util.*

class AppContextWrapper : ContextWrapper {

    constructor(context:Context?):super(context){}

    companion object{
        fun wrap(context: Context?, language: String):ContextWrapper{
            var inContext = context
            var config = inContext?.resources?.configuration
            var sysLocale:Locale
            if(config != null && inContext != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    sysLocale = getSystemLocale(config)
                } else {
                    sysLocale = getSystemLocaleLegacy(config)
                }
                if (language != "" && !sysLocale.language.equals(language)) {
                    var locale: Locale = Locale(language)
                    Locale.setDefault(locale)
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                        setSystemLocale(config, locale)
                    } else {
                        setSystemLocaleLegacy(config, locale)
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    inContext = inContext.createConfigurationContext(config);
                } else {
                    inContext.resources
                        .updateConfiguration(config, inContext.resources.displayMetrics);
                }
            }
            return AppContextWrapper(inContext)
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun getSystemLocale(config:Configuration):Locale{
            return config.locales.get(0)
        }

        @SuppressWarnings("deprecation")
        fun getSystemLocaleLegacy(config: Configuration):Locale{
            return config.locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun setSystemLocale(config: Configuration, locale: Locale){
            config.setLocale(locale)
        }

        @SuppressWarnings("deprecation")
        fun setSystemLocaleLegacy(config: Configuration, locale: Locale){
            config.locale = locale
        }
    }
}