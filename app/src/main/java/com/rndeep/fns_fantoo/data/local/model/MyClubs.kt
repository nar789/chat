package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "my_clubs")
data class MyClubs(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "club_id")
    val clubId: Long = 0L,

    @ColumnInfo(name = "club_image_url")
    var clubImageUrl: String = "",

    @ColumnInfo(name = "club_title")
    var clubTitle: String = "",

    @ColumnInfo(name = "club_member_count")
    var clubMemberCount: Int = 0,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Int = 0,

    @ColumnInfo(name = "is_admin")
    var isAdmin: Int = 0,

    @ColumnInfo(name = "is_locked")
    var isLocked: Int = 0
)