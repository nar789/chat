package com.rndeep.fns_fantoo.ui.chatting.imageviewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.chatting.compose.getChatImageUrl

@Composable
fun ImageViewerScreen(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onClickCancel: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = colorResource(R.color.black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ImageViewerHeader(onClickCancel = onClickCancel)
            ImageContentView(
                modifier = Modifier.fillMaxSize(),
                imageUrl = imageUrl
            )
        }
    }
}

@Composable
fun ImageViewerHeader(
    onClickCancel: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(start = 12.dp, top = 14.dp)
    ) {
        IconButton(onClick = { onClickCancel() }) {
            Icon(
                modifier = Modifier
                    .size(36.dp)
                    .padding(6.dp),
                painter = painterResource(R.drawable.icon_outline_cancel),
                contentDescription = null,
                tint = colorResource(R.color.state_enable_gray_25)
            )
        }
    }
}

@Composable
fun ImageContentView(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    var scale by remember { mutableStateOf(1f) }
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale *= zoom
                }
            },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl.getChatImageUrl(),
                fallback = painterResource(id = R.drawable.baby_icon),
                error = painterResource(id = R.drawable.baby_icon)
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    // adding some zoom limits (min 50%, max 200%)
                    scaleX = maxOf(.5f, minOf(3f, scale)),
                    scaleY = maxOf(.5f, minOf(3f, scale)),
                ),
        )

    }
}

@Preview
@Composable
fun ImageViewerScreenPreview() {
    MaterialTheme {
        ImageViewerScreen(
            imageUrl = "",
            onClickCancel = {}
        )
    }
}