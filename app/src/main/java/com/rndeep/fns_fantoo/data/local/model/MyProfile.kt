package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_profile")
data class MyProfile(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "",

    @ColumnInfo(name = "image_url")
    var imageUrl: String? = null,

    @ColumnInfo(name = "nickname")
    var nickname: String? = null,

    @ColumnInfo(name = "concern")
    var concern: String? = null,

    @ColumnInfo(name = "country")
    var country: String? = null,

    @ColumnInfo(name = "gender")
    var gender: GenderType = GenderType.UNKNOWN,

    @ColumnInfo(name = "birthday")
    var birthday: String? = null,

    @ColumnInfo(name = "login_type")
    var loginType: String? = null,
)