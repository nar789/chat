package com.rndeep.fns_fantoo.ui.chatting.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rndeep.fns_fantoo.R

@Composable
fun String?.getImageUrlFromCDN(): String? = when (this) {
    null -> null
    else -> stringResource(R.string.imageUrlBase, this)
}

@Composable
fun String?.getChatImageUrl(): String? = when (this) {
    null -> null
    else -> stringResource(id = R.string.chatting_image_prefix, this)
}