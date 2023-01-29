package com.rndeep.fns_fantoo.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rndeep.fns_fantoo.repositories.DataStoreKey.Companion.PREF_KEY_FCM_TOKEN
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import timber.log.Timber

class FantooFirebaseMessagingService: FirebaseMessagingService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FantooFirebaseMessagingServiceEntryPoint{
        fun dataStoreRepository() : DataStoreRepository
    }

    lateinit var dataStoreRepository : DataStoreRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.d("onMessageReceived $message")
        /*scope.launch {
            Timber.d("onMessageReceived token : ${dataStoreRepository.getString(PREF_KEY_FCM_TOKEN)}")
        }*/
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("onNewToken:$token")
        scope.launch {
            dataStoreRepository.putString(PREF_KEY_FCM_TOKEN, token)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, FantooFirebaseMessagingServiceEntryPoint::class.java)
        dataStoreRepository = entryPoint.dataStoreRepository()
    }
}