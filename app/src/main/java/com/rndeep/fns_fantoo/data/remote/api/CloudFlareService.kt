package com.rndeep.fns_fantoo.data.remote.api

import com.rndeep.fns_fantoo.data.remote.model.CloudFlareImage
import com.rndeep.fns_fantoo.data.remote.model.CloudFlareVideo
import okhttp3.MultipartBody
import retrofit2.http.*

interface CloudFlareService {

    @Multipart
    @POST("/client/v4/accounts/{accountKey}/images/v1")
    suspend fun uploadImage(@Header("Authorization") body:String, @Path("accountKey")accountKey :String, @Part file : MultipartBody.Part) : CloudFlareImage

    //200MB 보다 작은 파일의 경우 사용
    @Multipart
    @POST("/client/v4/accounts/{accountKey}/stream")
    suspend fun uploadVideo(@Header("Authorization") body:String, @Path("accountKey")accountKey :String, @Part file : MultipartBody.Part) : CloudFlareVideo

    //이미지 삭제
    @DELETE("client/v4/accounts/{accountKey}/images/v1/{imageId}")
    suspend fun deleteImage(@Header("Authorization") body:String, @Path("accountKey")accountKey :String,@Path("imageId")imageId :String)

}