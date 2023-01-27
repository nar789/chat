package com.rndeep.fns_fantoo.ui.chatting.imagepicker

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.paging.compose.LazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.rndeep.fns_fantoo.R

@Composable
fun ImagePickerScreen(
    images: LazyPagingItems<Uri>,
    checkedImages: List<Uri>,
    onClickCancel: () -> Unit,
    onClickDone: () -> Unit,
    onClickImage: (image: Uri) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = rememberAsyncImagePainter(
                    ContextCompat.getDrawable(LocalContext.current, R.drawable.bg_popup_2),
                ),
                contentScale = ContentScale.FillBounds
            ),
        color = Color.Transparent
    ) {
        Column {
            PickerSelector(
                modifier = Modifier.fillMaxWidth(),
                onClickCancel = onClickCancel,
                onClickDone = onClickDone,
                imageCount = checkedImages.size
            )
            ImageGridContents(
                modifier = Modifier.fillMaxSize(),
                onClickImage = onClickImage,
                images = images,
                checkedImages = checkedImages
            )
        }
    }
}

@Composable
fun PickerSelector(
    modifier: Modifier = Modifier,
    onClickCancel: () -> Unit,
    onClickDone: () -> Unit,
    imageCount: Int,
    doneBtnEnable: Boolean = true
) {
    Row(
        modifier = modifier
            .height(58.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp, start = 18.dp)
                .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(bounded = false)
                    ) { onClickCancel() },
            text = stringResource(R.string.image_picker_cancel_btn),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colorResource(R.color.state_enable_gray_900),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.image_picker_title),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = colorResource(R.color.gray_900),
                textAlign = TextAlign.Center
            )
            Text(
                text = String.format(stringResource(R.string.image_picker_sub_title), imageCount),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = colorResource(R.color.gray_900),
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 20.dp, end = 18.dp)
                .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(bounded = false)
                    ) { onClickDone() },
            text = stringResource(R.string.image_picker_done_btn),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colorResource(R.color.state_active_primary_default),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ImageGridContents(
    modifier: Modifier = Modifier,
    images: LazyPagingItems<Uri>,
    checkedImages: List<Uri>,
    onClickImage: (Uri) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(images.itemCount) { index ->
            val item = images[index]
            ImagePickerItem(
                image = item ?: return@items,
                selected = checkedImages.contains(item),
                onClickImage = onClickImage
            )
        }
    }
}

@Composable
fun ImagePickerItem(
    image: Uri,
    selected: Boolean,
    onClickImage: (Uri) -> Unit
) {
    Box(
        modifier = Modifier.clickable { onClickImage(image) },
        contentAlignment = Alignment.Center
    ) {
        val colorFilter = if (selected) {
            ColorFilter.tint(colorResource(R.color.gray_900).copy(alpha = 0.4f), BlendMode.SrcOver)
        } else {
            null
        }

        val defaultImage = painterResource(R.drawable.profile_character1)
        Image(
            modifier = Modifier
                .aspectRatio(1f)
                .size(118.dp),
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter,
            painter = rememberAsyncImagePainter(
                model = image,
                fallback = defaultImage,
                error = defaultImage
            ), contentDescription = null
        )

        if (selected) {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(R.drawable.checkbox_round_check),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun ImagePickerScreenPreview() {
//    ImagePickerScreen(, emptyList(), {}, {}, {})
}

val testImages =
    (0..30).map { "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832" }