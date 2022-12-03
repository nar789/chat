package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "tb_popular_trend_tag")
data class TrendTagItem (
    @PrimaryKey
    var _id:String ,
    @ColumnInfo(name = "popular_trend_name")
    var trendTagName :String

)