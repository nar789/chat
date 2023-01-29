package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_home_banner")
data class BannerItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "banner_id")
    val _id:Long?,
    @ColumnInfo(name = "banner_image_url")
    val imageUrl:String,
    @ColumnInfo(name = "banner_move_link")
    val moveLink:String,
)
