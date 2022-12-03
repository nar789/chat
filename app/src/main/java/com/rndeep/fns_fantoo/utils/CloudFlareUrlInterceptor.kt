package com.rndeep.fns_fantoo.utils

import android.util.Patterns
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.net.URL
import javax.inject.Inject

class CloudFlareUrlInterceptor @Inject constructor(private val dataStoreRepository: DataStoreRepository): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var configUrl =
            runBlocking { dataStoreRepository.getString(DataStoreKey.PREF_KEY_CLOUDFLARE_URL) }
        if(!configUrl.isNullOrEmpty() && isValidUrl(configUrl)){
            val hostUrl = configUrl.toHttpUrlOrNull()?.host
            val port = configUrl.toHttpUrlOrNull()?.port //URL(apiUrl).port
            hostUrl?.let {
                //Timber.d("pre url = ${request.url}, hostUrl = $hostUrl, apiUrl = $configUrl , port : $port")
                val builder = request.url.newBuilder()
                builder.host(hostUrl)
                port?.let {
                    builder.port(port)
                }
                val newApiUrl = builder.build()
                val newRequest = request.newBuilder().url(newApiUrl).build()
                return chain.proceed(newRequest)
            }
        }
        return chain.proceed(request)
    }

    private fun isValidUrl(url:String):Boolean{
        return if(url.isNullOrEmpty()){
            false
        }else{
            Patterns.WEB_URL.matcher(url).matches()
        }
    }
}