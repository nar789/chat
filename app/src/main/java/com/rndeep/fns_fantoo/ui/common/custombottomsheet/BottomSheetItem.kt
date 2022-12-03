package com.rndeep.fns_fantoo.ui.common.custombottomsheet

data class BottomSheetItem(
    var imageId :Int?,
    var itemName :String,
    var subText : String?=null,
    var isChecked :Boolean?,
    var selectLangCode : String?=null
)
