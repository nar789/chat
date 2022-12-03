package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_comment")
data class LibraryComment(
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

    @ColumnInfo(name = "comment")
    var comment: String = "",

    @ColumnInfo(name = "original_contents")
    var originalContents: String = "",

    @ColumnInfo(name = "comment_image_url")
    var commentImageUrl: String = "",

    @ColumnInfo(name = "comment_type")
    var commentType: Int = 0
)
