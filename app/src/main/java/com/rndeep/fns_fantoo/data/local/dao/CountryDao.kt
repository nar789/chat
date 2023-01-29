package com.rndeep.fns_fantoo.data.local.dao

import androidx.room.*
import com.rndeep.fns_fantoo.data.local.model.Country

@Dao
interface CountryDao{

    @Query("SELECT * FROM tb_country")
    suspend fun getAllCountry():List<Country>

    @Query("Delete FROM tb_country")
    suspend fun deleteAllCountry()

    @Query("SELECT * FROM tb_country WHERE country_code = :countryCode")
    suspend fun getCountry(countryCode:String):Country?

    @Query("SELECT * FROM tb_country WHERE iso2 = :iso2")
    suspend fun getCountryByIso2(iso2: String):Country?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: Country)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCountry(countryList:List<Country>)

    @Update
    suspend fun updateCountry(country: Country)

    @Query("SELECT country_code FROM tb_country  WHERE iso2 = :iso2")
    suspend fun getCountryCodeByIso2(iso2: String): String

    suspend fun insertOrUpdateCountry(country: Country){
        val countryInDB = getCountry(country.countryCode)
        if(countryInDB == null){
            insertCountry(country)
        }else{
            updateCountry(country)
        }
    }
}
