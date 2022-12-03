package com.rndeep.fns_fantoo.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rndeep.fns_fantoo.data.remote.api.*
import com.rndeep.fns_fantoo.utils.ConstVariable.BASE_API_URL
import com.rndeep.fns_fantoo.utils.ConstVariable.BASE_URL
import com.rndeep.fns_fantoo.utils.ConstVariable.CLOUDFLARE_URL
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.utils.ApiUrlInterceptor
import com.rndeep.fns_fantoo.utils.CloudFlareUrlInterceptor
import com.rndeep.fns_fantoo.utils.ConstVariable.TRANSLATE_URL
import com.rndeep.fns_fantoo.utils.TokenRefreshInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ApiServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CloudFlareServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TranslateServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ApiOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CloudFlareServerHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TranslateServerHttpClient

    @AuthServer
    @Provides
    fun provideBaseUrl() = BASE_URL

    @ApiServer
    @Provides
    fun provideBaseApiUrl() = BASE_API_URL

    @CloudFlareServer
    @Provides
    fun provideCloudFlareApiUrl() = CLOUDFLARE_URL

    @TranslateServer
    @Provides
    fun provideTransLateServer() = TRANSLATE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(dataStoreRepository: DataStoreRepository): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(TokenRefreshInterceptor(dataStoreRepository))
            .build()
    }

    @ApiOkHttpClient
    @Singleton
    @Provides
    fun provideApiOkHttpClient(dataStoreRepository: DataStoreRepository):OkHttpClient{
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(provideApiUrlInterceptor(dataStoreRepository))
            .addInterceptor(TokenRefreshInterceptor(dataStoreRepository))
            .build()
    }

    @CloudFlareServerHttpClient
    @Singleton
    @Provides
    fun provideCloudFlareOkHttpClient(dataStoreRepository: DataStoreRepository):OkHttpClient{
//        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
//            .addInterceptor(logger)

        return OkHttpClient.Builder()
            .addInterceptor(provideCloudFlareUrlInterceptor(dataStoreRepository))
            .addInterceptor(TokenRefreshInterceptor(dataStoreRepository))
            .build()
    }

    @TranslateServerHttpClient
    @Singleton
    @Provides
    fun provideTranslateOKHttpClient():OkHttpClient{
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @ApiServer
    @Singleton
    @Provides
    fun provideRetrofit(@ApiOkHttpClient client: OkHttpClient, @ApiServer baseApiUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseApiUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @AuthServer
    @Singleton
    @Provides
    fun provideAuthRetrofit(client: OkHttpClient, @AuthServer baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @CloudFlareServer
    @Singleton
    @Provides
    fun provideCloudFlareRetrofit(@CloudFlareServerHttpClient client: OkHttpClient,@CloudFlareServer baseUrl: String) : Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @TranslateServer
    @Singleton
    @Provides
    fun provideTranslateRetrofit(@TranslateServerHttpClient client: OkHttpClient,@TranslateServer baseUrl: String) : Retrofit
        = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @AuthServer
    @Singleton
    @Provides
    fun provideAuthService(@AuthServer retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @ApiServer
    @Singleton
    @Provides
    fun provideClubService(@ApiServer retrofit: Retrofit): ClubService {
        return retrofit.create(ClubService::class.java)
    }

    @ApiServer
    @Singleton
    @Provides
    fun provideUserInfoService(@ApiServer retrofit: Retrofit): UserInfoService {
        return retrofit.create(UserInfoService::class.java)
    }

    @ApiServer
    @Singleton
    @Provides
    fun provideCommonService(@ApiServer retrofit: Retrofit):CommonService{
        return retrofit.create(CommonService::class.java)
    }

    @ApiServer
    @Singleton
    @Provides
    fun provideRegistService(@ApiServer retrofit: Retrofit):RegistService{
        return retrofit.create(RegistService::class.java)
    }

    @ApiServer
    @Singleton
    @Provides
    fun providePostDetailService(@ApiServer retrofit: Retrofit) :PostDetailService=retrofit.create(PostDetailService::class.java)

    @ApiServer
    @Singleton
    @Provides
    fun provideCommunityService(@ApiServer retrofit: Retrofit) : CommunityService=retrofit.create(CommunityService::class.java)

    @ApiServer
    @Singleton
    @Provides
    fun provideHomeService(@ApiServer retrofit: Retrofit) : HomeService =retrofit.create(HomeService::class.java)

    @CloudFlareServer
    @Singleton
    @Provides
    fun provideCloudFlareService(@CloudFlareServer retrofit: Retrofit) :CloudFlareService =retrofit.create(CloudFlareService::class.java)

    @TranslateServer
    @Singleton
    @Provides
    fun provideTranslateService(@TranslateServer retrofit: Retrofit) : TranslateService =retrofit.create(TranslateService::class.java)

    @ApiServer
    @Singleton
    @Provides
    fun providePostService(@ApiServer retrofit: Retrofit) :PostService=retrofit.create(PostService::class.java)

    @ApiServer
    @Singleton
    @Provides
    fun provideFantooClubService(@ApiServer retrofit: Retrofit): FantooClubService {
        return retrofit.create(FantooClubService::class.java)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideApiUrlInterceptor(dataStoreRepository: DataStoreRepository):ApiUrlInterceptor{
        return ApiUrlInterceptor(dataStoreRepository)
    }

    @Singleton
    @Provides
    fun provideCloudFlareUrlInterceptor(dataStoreRepository: DataStoreRepository):CloudFlareUrlInterceptor{
        return CloudFlareUrlInterceptor(dataStoreRepository)
    }
}