package com.rndeep.fns_fantoo.data.remote.api

import com.google.gson.JsonObject
import com.rndeep.fns_fantoo.data.local.model.Language
import com.rndeep.fns_fantoo.data.remote.dto.TranslateDTO
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslateService {

    @POST("/trans")
    suspend fun getTranslateWord(
        @Body hashBody: JsonObject
    ) : TranslateDTO

}