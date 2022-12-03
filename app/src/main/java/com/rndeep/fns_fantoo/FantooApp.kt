package com.rndeep.fns_fantoo

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.security.MessageDigest

@HiltAndroidApp
class FantooApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //getHashKey()

        FirebaseApp.initializeApp(this)
        KakaoSdk.init(this, getString(R.string.kakaoApiKey))

        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getHashKey(){
        val packageInfo: PackageInfo?
        try{
            packageInfo = this.packageManager.getPackageInfo(this.packageName,
            PackageManager.GET_SIGNING_CERTIFICATES)

            if(packageInfo != null) {
                val signatues = packageInfo.signingInfo.apkContentsSigners
                val md :MessageDigest = MessageDigest.getInstance("SHA")
                for (sig in signatues) {
                    md.update(sig.toByteArray())
                    val signatureBase64 = String(Base64.encode(md.digest(), Base64.DEFAULT))
                    Timber.d("Signature hashKey = $signatureBase64")
                    //MeHL0o4HJhMAwxw2KFaB7SIOhy8=
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}
