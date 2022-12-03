package com.rndeep.fns_fantoo.ui.login

interface SimpleLoginListener {
    fun onSuccess(email:String?, socialSite:String?, bool:String?, sub:String?, snsId:String?)
}