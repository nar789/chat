package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_language")
data class Language(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name_kr")
    val nameKr: String = "",

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "lang_code")
    val langCode: String = ""
)

fun Language.toLanguageDTO() = com.rndeep.fns_fantoo.data.remote.model.Language(
    nameKr = nameKr,
    name = name,
    langCode = langCode
)