package com.rndeep.fns_fantoo.data.remote.api

import com.google.gson.JsonObject
import com.rndeep.fns_fantoo.data.remote.dto.TranslateChatDto
import com.rndeep.fns_fantoo.data.remote.dto.TranslateDTO
import com.rndeep.fns_fantoo.data.remote.dto.TranslationRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TranslateService {

    @POST("/trans")
    suspend fun getTranslateWord(
        @Body hashBody: JsonObject
    ): TranslateDTO

    @POST("/trans")
    suspend fun getTranslateWord(
        @Header("Auth-Token") authToken: String,
        @Header("Device-ID") deviceId: String,
        @Body translationRequest: TranslationRequest
    ): TranslateChatDto

}