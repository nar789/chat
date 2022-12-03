package com.rndeep.fns_fantoo.utils

import android.os.Build
import com.rndeep.fns_fantoo.BuildConfig
import com.rndeep.fns_fantoo.data.remote.api.AuthService
import com.rndeep.fns_fantoo.repositories.DataStoreKey.Companion.PREF_KEY_ACCESS_TOKEN
import com.rndeep.fns_fantoo.repositories.DataStoreKey.Companion.PREF_KEY_REFRESH_ACCESS_TOKEN
import com.rndeep.fns_fantoo.repositories.DataStoreKey.Companion.PREF_KEY_SYSTEM_COUNTRY
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_ACCESS_TOKEN
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.net.HttpURLConnection
import javax.inject.Inject


class TokenRefreshInterceptor @Inject constructor(private val dataStoreRepository: DataStoreRepository) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest: Request?
        val systemCountryCode = runBlocking {
            dataStoreRepository.getString(PREF_KEY_SYSTEM_COUNTRY)?:""
        }
        /*var appLangCode =
            runBlocking { dataStoreRepository.getString(PREF_KEY_LANGUAGE_CODE) }*/
        val accessToken =
            runBlocking { dataStoreRepository.getString(PREF_KEY_ACCESS_TOKEN) }
        /*if (appLangCode != null) {
            when (appLangCode) {
                LanguageUtils.CHINESE_SIMPLIFIED -> {
                    appLangCode = "zh-cn"
                }
                LanguageUtils.CHINESE_TRADITIONAL -> {
                    appLangCode = "zh-tw"
                }
            }
        } else {
            appLangCode = LanguageUtils.ENGLISH
        }*/

        val url = request.url.newBuilder()
            .addQueryParameter(
                ConstVariable.Config.KEY_COMMON_PARAM_APP_VERSION,
                BuildConfig.VERSION_NAME
            )
            .addQueryParameter(
                ConstVariable.Config.KEY_COMMON_PARAM_DEVICE,
                ConstVariable.Config.VALUE_DEVICE_TYPE
            )
            .addQueryParameter(
                ConstVariable.Config.KEY_COMMON_PARAM_OS_VERSION,
                Build.VERSION.RELEASE
            )
            .addQueryParameter(ConstVariable.Config.KEY_COMMON_PARAM_COUNTRY, systemCountryCode)
            .build()
        val urlStrings = url.toString().split("/")
        var refererUrl = ""
        try {
            refererUrl = urlStrings[0] + "/" + urlStrings[1] + "/" + urlStrings[2] + "/" + urlStrings[3]
        }catch (e:Exception){
            Timber.e("${e.printStackTrace()}")
        }
        //Timber.d("version : ${BuildConfig.VERSION_NAME} , os : ${Build.VERSION.RELEASE} , url : $refererUrl , country : $systemCountryCode")
        val newRequestBuilder: Request.Builder = request.newBuilder()
            //.addHeader(ConstVariable.KEY_HEADER_LANG, appLangCode)
            .addHeader(ConstVariable.Config.KEY_HEADER_REFERER, refererUrl)
            .addHeader(
                ConstVariable.Config.KEY_HEADER_USER_AGENT,
                ConstVariable.Config.VALUE_HEADER_USER_AGENT
            )
        newRequestBuilder.url(url)
        newRequest = if (accessToken != null) {
            newRequestBuilder.addHeader(KEY_ACCESS_TOKEN, accessToken).build()
            //Timber.d("intercept add header")
        } else {
            newRequestBuilder.build()
        }

        val response = chain.proceed(newRequest)
        //Timber.d("intercept , ${request.toString()}")
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            Timber.d("intercept , HTTP_UNAUTHORIZED !!!")
            val refreshToken =
                runBlocking { dataStoreRepository.getString(PREF_KEY_REFRESH_ACCESS_TOKEN) }
            Timber.d("intercept refreshtoken = $refreshToken")
            refreshToken?.let {
                val newToken = runBlocking { getNewToken(ConstVariable.BASE_URL, refreshToken) }
                if (newToken.isNotEmpty()) {
                    runBlocking {
                        dataStoreRepository.putString(PREF_KEY_ACCESS_TOKEN, newToken)
                    }
                    response.close()
                    val reNewRequest =
                        request.newBuilder().header(KEY_ACCESS_TOKEN, newToken).build()
                    //Timber.d("request = $reNewRequest")
                    return chain.proceed(reNewRequest)
                }
            }
        }
        return response
    }

    private suspend fun getNewToken(baseUrl: String, refreshToken: String): String {
        var newToken = ""
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val authService = retrofit.create(AuthService::class.java)
        try {
            val response = authService.getAccessToken(
                hashMapOf(
                    ConstVariable.Auth.KEY_CLIENT_ID to ConstVariable.Auth.VALUE_CLIENT_ID,
                    ConstVariable.Auth.KEY_CLIENT_SECRET to ConstVariable.Auth.VALUE_CLIENT_SECRET,
                    ConstVariable.Auth.KEY_GRANT_TYPE to ConstVariable.Auth.VALUE_GRANT_TYPE_REFRESH_TOKEN,
                    ConstVariable.Auth.KEY_REFRESH_TOKEN to refreshToken
                )
            )
            //Timber.d("getNewToken responese  = $response")
            newToken = response.accessToken
        } catch (e: Exception) {
            Timber.e("getNewToken err: ${e.message}")
        }
        return newToken
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

}