package com.rndeep.fns_fantoo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CountryResponse(
    @field:SerializedName("coinStatus") val coinStatus: Int,
    @field:SerializedName("countryCode") val countryCode: String,
    @field:SerializedName("countryNum") val countryNum: String?,
    @field:SerializedName("iso2") val iso2: String,
    @field:SerializedName("iso3") val iso3: String,
    @field:SerializedName("nameEn") val nameEn: String,
    @field:SerializedName("nameKr") val nameKr: String
)
