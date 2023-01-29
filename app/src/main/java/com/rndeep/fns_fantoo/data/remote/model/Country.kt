package com.rndeep.fns_fantoo.data.remote.model

import com.google.gson.annotations.SerializedName

data class Country(
    @field:SerializedName("countryCode") val countryCode: String,
    @field:SerializedName("countryNum") val countryNum: String?,
    @field:SerializedName("nameKr") val nameKr: String,
    @field:SerializedName("nameEn") val nameEn: String,
    @field:SerializedName("iso2") val iso2: String,
    @field:SerializedName("iso3") val iso3: String,
    @field:SerializedName("coinStatus") val coinStatus: Int
)

fun Country.toCountryEntity() = com.rndeep.fns_fantoo.data.local.model.Country(
   countryCode = countryCode,
    countryNum = countryNum,
    nameKr = nameKr,
    nameEn = nameEn,
    iso2 = iso2,
    iso3 = iso3,
    coinStatus = coinStatus
)
