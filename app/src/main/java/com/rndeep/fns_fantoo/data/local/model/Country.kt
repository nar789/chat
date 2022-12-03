package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_country")
data class Country(
    @PrimaryKey(autoGenerate = true)
    val id :Int = 0,

    @ColumnInfo(name = "country_code")
    val countryCode: String = "",

    @ColumnInfo(name = "country_num")
    val countryNum: String? = "",

    @ColumnInfo(name = "name_kr")
    val nameKr: String = "",

    @ColumnInfo(name = "name_en")
    val nameEn: String = "",

    @ColumnInfo(name = "iso2")
    val iso2: String = "",

    @ColumnInfo(name = "iso3")
    val iso3: String = "",

    @ColumnInfo(name = "coin_status")
    val coinStatus: Int = 0
)

fun Country.toCountryDTO() = com.rndeep.fns_fantoo.data.remote.model.Country(
    countryCode = countryCode,
    countryNum = countryNum,
    nameKr = nameKr,
    nameEn = nameEn,
    iso2 = iso2,
    iso3 = iso3,
    coinStatus = coinStatus
)