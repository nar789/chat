package com.rndeep.fns_fantoo.ui.menu

data class DialogMessage(
    val title: DialogTitle,
    val button: DialogButton,
    val isCompleted: Boolean
)

data class DialogTitle(
    val title: String?,
    val subTitle: String?,
    val extraMessage: String?,
)

data class DialogButton(
    val okTitle: String,
    val cancelTitle: String?,
    val isTwoButton: Boolean
)