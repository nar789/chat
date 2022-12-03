package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_save")
data class LibrarySave(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "image_url")
    var imageUrl: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "nickname")
    var nickname: String = "",

    @ColumnInfo(name = "time")
    var time: Long = 0L,

    @ColumnInfo(name = "contents")
    var contents: String = "",

    @ColumnInfo(name = "honor")
    var honor: String = "",

    @ColumnInfo(name = "like_count")
    var likeCount: Int = 0,

    @ColumnInfo(name = "comment_count")
    var commentCount: Int = 0,

    @ColumnInfo(name = "save_type")
    var saveType: Int = 0
)
