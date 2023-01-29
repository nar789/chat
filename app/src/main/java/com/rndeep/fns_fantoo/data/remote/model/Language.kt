package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class Language(
    @field:SerializedName("nameKr") val nameKr: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("langCode") val langCode: String
)

fun Language.toLanguageEntity() = com.rndeep.fns_fantoo.data.local.model.Language(
    nameKr = nameKr,
    name = name,
    langCode = langCode
)