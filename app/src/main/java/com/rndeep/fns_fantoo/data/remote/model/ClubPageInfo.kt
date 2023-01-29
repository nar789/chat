package com.rndeep.fns_fantoo.data.remote.model

import androidx.fragment.app.Fragment
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


data class ClubPagePostListType(
    var type:String,
    var fragment: Fragment
)