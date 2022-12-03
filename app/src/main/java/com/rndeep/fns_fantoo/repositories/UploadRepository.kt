package com.rndeep.fns_fantoo.repositories

import android.content.SharedPreferences
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.CloudFlareService
import com.rndeep.fns_fantoo.data.remote.model.CloudFlareImage
import com.rndeep.fns_fantoo.data.remote.model.CloudFlareVideo
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import io.tus.android.client.TusPreferencesURLStore
import io.tus.java.client.TusClient
import io.tus.java.client.TusExecutor
import io.tus.java.client.TusUpload
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.net.URL
import javax.inject.Inject

class UploadRepository @Inject constructor(
    @NetworkModule.CloudFlareServer private val cloudFlareService: CloudFlareService
) : BaseNetRepo() {

    suspend fun imageUploadToCloudFlare(
        cloudFlareKey: String,
        file: MultipartBody.Part
    ): ResultWrapper<CloudFlareImage> = safeApiCall(Dispatchers.IO) {
        cloudFlareService.uploadImage(
            ConstVariable.CloudFlare.IMAGEAPIKEY,
            cloudFlareKey,
            file
        )
    }

    suspend fun videoUploadToCloudFlare(
        cloudFlareKey: String,
        file: MultipartBody.Part
    ): ResultWrapper<CloudFlareVideo> = safeApiCall(Dispatchers.IO) {
        cloudFlareService.uploadVideo(
            ConstVariable.CloudFlare.VIDEOAPIKET,
            cloudFlareKey,
            file
        )
    }

    //이미지 클라우드 플레어 전송
    suspend fun fileUploadImageToCloudFlare(
        cloudFlareKey: String,
        file: MultipartBody.Part
    ): String? {
        val res = safeApiCall(Dispatchers.IO) {
            cloudFlareService.uploadImage(
                ConstVariable.CloudFlare.IMAGEAPIKEY,
                cloudFlareKey,
                file
            )
        }
        return when (res) {
            is ResultWrapper.Success -> {
                res.data.result.id
            }
            is ResultWrapper.GenericError -> {
                null
            }
            is ResultWrapper.NetworkError -> {
                null
            }
        }
    }

    //영상 전송
    suspend fun fileUploadVideoToCloudFlare(
        cloudFlareKey: String,
        file: MultipartBody.Part
    ): String? {
        val res = safeApiCall(Dispatchers.IO) {
            cloudFlareService.uploadVideo(
                ConstVariable.CloudFlare.VIDEOAPIKET,
                cloudFlareKey,
                file
            )
        }
        return when (res) {
            is ResultWrapper.Success -> {
                res.data.result?.uid
            }
            is ResultWrapper.GenericError -> {
                null
            }
            is ResultWrapper.NetworkError -> {
                null
            }
        }
    }

    suspend fun fileDeleteImage(cloudFlareKey: String,imageId:String){
        safeApiCall(Dispatchers.IO){
            cloudFlareService.deleteImage(
                ConstVariable.CloudFlare.IMAGEAPIKEY,
                cloudFlareKey,
                imageId
            )
        }
    }

    suspend fun tusVideoUpload(resumingPreferences: SharedPreferences, videoFile : File, cloudFlareKey: String, fileName:String) : String?{
        val tusPreferences=TusPreferencesURLStore(resumingPreferences)
        safeApiCall(Dispatchers.IO){
            tusSetting(tusPreferences,videoFile, cloudFlareKey, fileName)
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data
                }
                is ResultWrapper.GenericError->{
                    null
                }
                is ResultWrapper.NetworkError->{
                    null
                }
            }
        }
    }

    fun tusSetting(tusPreferences :TusPreferencesURLStore,videoFile : File, cloudFlareKey: String, fileName:String) : String?{
        var cloudFlareId :String?=null
        val client = TusClient().apply {
            uploadCreationURL= URL("${ConstVariable.CLOUDFLARE_URL}/client/v4/accounts/$cloudFlareKey/stream")
            headers=HashMap<String,String>().apply {
                this["Authorization"]=ConstVariable.CloudFlare.VIDEOAPIKET
            }
            enableResuming(tusPreferences)
        }
        val upload = TusUpload(videoFile).apply {
            metadata["name"]=fileName
        }
        val executor = object : TusExecutor(){
            override fun makeAttempt() {
                val uploader = client.resumeOrCreateUpload(upload)
                uploader.chunkSize= 5*1024*1024
                do{
                    val totalBytes = upload.size
                    val bytesUploaded = uploader.offset
                    val progress = (bytesUploaded*100f/totalBytes)
                    Timber.d("cloud Flare vide uploading present ${progress}% / [uploadChunk] : ${uploader.uploadChunk()}")
                }while (uploader.uploadChunk() >-1)
                uploader.finish()
                val str=uploader.uploadURL.toString().substring(
                    uploader.uploadURL.toString().lastIndexOf("media/")+("media/".length)
                )
                cloudFlareId=str.substring(0,str.indexOf("?"))
                if(tusPreferences.get(upload.fingerprint)!=null){
                    tusPreferences.remove(upload.fingerprint)
                }
            }
        }
        executor.makeAttempts()
        return cloudFlareId

    }


}