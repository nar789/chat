package com.rndeep.fns_fantoo.data.remote.model

data class MyClubList(
    val msg :String,
    val code:String,
    val dataObj: ArrayList<ClubList>
)

data class ClubList(
    val clubManageId:Int,
    val profileImgUrl:String?,
    val clubName:String,
    val memberNum:Int,
    val isOwner:Boolean,
    val isView:Boolean,
    var bookmark : Boolean=false
)

