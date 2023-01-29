package com.rndeep.fns_fantoo.data.remote.dto

data class AccessTokenResponse (
    val code:String?,
    val msg:String?,
    val accessTokenDto: AccessTokenDto?
)