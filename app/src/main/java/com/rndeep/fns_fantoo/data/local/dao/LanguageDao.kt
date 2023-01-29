package com.rndeep.fns_fantoo.data.local.dao

import androidx.room.*
import com.rndeep.fns_fantoo.data.local.model.Country
import com.rndeep.fns_fantoo.data.local.model.Language

@Dao
interface LanguageDao {

    @Query("SELECT * FROM tb_language")
    suspend fun getAllLanguage():List<Language>

    @Query("SELECT * FROM tb_language WHERE lang_code = :langCode")
    suspend fun getLanguage(langCode:String):Language?

    @Query("Delete FROM tb_language")
    suspend fun deleteAllLanguage()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(language: Language)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLanguage(languageList:List<Language>)

    @Update
    suspend fun updateLanguage(language: Language)

    suspend fun insertOrUpdate(language: Language){
        val lang = getLanguage(language.langCode)
        if(lang == null){
            insertLanguage(language)
        }else{
            updateLanguage(language)
        }
    }

}