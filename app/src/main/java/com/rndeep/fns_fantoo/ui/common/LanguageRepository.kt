package com.rndeep.fns_fantoo.ui.common

import com.rndeep.fns_fantoo.data.local.dao.CountryDao
import com.rndeep.fns_fantoo.data.local.dao.LanguageDao
import com.rndeep.fns_fantoo.data.local.model.Country
import com.rndeep.fns_fantoo.data.local.model.Language
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.CommonService
import com.rndeep.fns_fantoo.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class LanguageRepository @Inject constructor(
    @NetworkModule.ApiServer private val commonService: CommonService,
    private val countryDao: CountryDao, private val languageDao: LanguageDao
) :
    BaseNetRepo() {

    suspend fun getCountryAll(): ResultWrapper<List<com.rndeep.fns_fantoo.data.remote.model.Country>> = safeApiCall(
        Dispatchers.IO
    ) {
        commonService.getCountryAll()
    }

    suspend fun getSupportLanguageAll(): ResultWrapper<List<com.rndeep.fns_fantoo.data.remote.model.Language>> =
        safeApiCall(
            Dispatchers.IO
        ) {
            commonService.getSupportLanguageAll()
        }

    suspend fun getCountryAllDB(): List<Country> {
        return countryDao.getAllCountry()
    }

    suspend fun addAllCountryDB(countryList: List<Country>) {
        countryDao.insertAllCountry(countryList)
    }

    suspend fun deleteAllCountryDB() {
        countryDao.deleteAllCountry()
    }

    suspend fun insertOrUpdateCountry(country: Country){
        countryDao.insertOrUpdateCountry(country)
    }

    suspend fun getLanguageAllDB(): List<Language>{
        return languageDao.getAllLanguage()
    }

    suspend fun addAllLanguageToDB(languageList:List<Language>){
        languageDao.insertAllLanguage(languageList)
    }

    suspend fun deleteAllLanguageDB(){
        languageDao.deleteAllLanguage()
    }

    suspend fun insertOrUpdateLanguage(language: Language){
        languageDao.insertOrUpdate(language)
    }

    suspend fun getCountryCodeByIso2(iso2: String) : String {
        return countryDao.getCountryCodeByIso2(iso2)
    }
}