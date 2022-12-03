package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_popular_curation")
data class CurationDataItem(
    @PrimaryKey(autoGenerate = true)
    var pkID:Int=0,
    @ColumnInfo(name = "curation_id")
    var id :String="",
    @ColumnInfo(name = "curation_text")
    var curationText:String="",
    @ColumnInfo(name = "curation_image")
    var curationImage:String=""

)
