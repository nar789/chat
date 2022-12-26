package com.rndeep.fns_fantoo.ui.chatting.profiledetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography

@Composable
fun ProfileDetailScreen(
    profileImage: String,
    userBlocked: Boolean = false,
    userFollowed: Boolean = false,
    onClickCancel: () -> Unit,
    onClickBlock: (Boolean) -> Unit,
    onClickFollow: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        ProfileDetailContent(
            modifier = Modifier.padding(top = 27.dp),
            userName = "Dasol",
            userBlocked = userBlocked,
            userFollowed = userFollowed,
            onClickCancel = onClickCancel,
            onClickBlock = onClickBlock,
            onClickFollow = onClickFollow
        )

        ProfileImage(
            modifier = Modifier,
            imageUrl = profileImage
        )
    }
}

@Composable
fun ProfileDetailContent(
    modifier: Modifier = Modifier,
    userName: String,
    userBlocked: Boolean,
    userFollowed: Boolean = false,
    onClickCancel: () -> Unit,
    onClickBlock: (Boolean) -> Unit,
    onClickFollow: (Boolean) -> Unit
) {
    Surface(
        modifier = modifier
            .width(276.dp)
            .heightIn(192.dp),
        shape = RoundedCornerShape(32.dp),
        color = colorResource(R.color.gray_25)
    ) {
        Column {
            IconButton(
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp)
                    .size(36.dp)
                    .padding(6.dp)
                    .align(Alignment.End),
                onClick = onClickCancel
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_outline_cancel),
                    contentDescription = null
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                text = userName,
                color = colorResource(R.color.gray_870),
                style = FantooChatTypography.subtitle2.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 29.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val followBtnName =
                    stringResource(if (userFollowed) R.string.profile_detail_following_btn else R.string.profile_detail_follow_btn)
                IconSelector(
                    modifier = Modifier,
                    imageSource = painterResource(R.drawable.icon_outline_addfriend),
                    buttonName = followBtnName,
                    checked = userFollowed,
                    onClick = onClickFollow
                )

                val blockBtnName =
                    stringResource(if (userBlocked) R.string.profile_detail_un_block_btn else R.string.profile_detail_block_btn)
                IconSelector(
                    modifier = Modifier,
                    imageSource = painterResource(R.drawable.icon_outline_blockaccount),
                    buttonName = blockBtnName,
                    checked = userBlocked,
                    onClick = onClickBlock
                )
            }
        }
    }
}

@Composable
fun IconSelector(
    modifier: Modifier,
    imageSource: Painter,
    buttonName: String,
    checked: Boolean,
    onClick: (Boolean) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (buttonColor, textColor) = if (checked) {
            colorResource(R.color.bg_light_gray_50) to colorResource(R.color.primary_500)
        } else {
            colorResource(R.color.primary_500) to colorResource(R.color.gray_25)
        }

        Image(
            modifier = Modifier
                .padding(start = 34.dp, end = 34.dp, top = 14.dp)
                .size(32.dp),
            painter = imageSource,
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.size(8.dp))
        Surface(
            modifier = Modifier
                .height(26.dp)
                .widthIn(50.dp)
                .clickable { onClick(!checked) },
            shape = RoundedCornerShape(6.dp),
            color = buttonColor,
            border = BorderStroke(1.dp, colorResource(R.color.primary_500)),
        ) {
            Text(
                modifier = Modifier.padding(3.dp, 5.dp),
                text = buttonName,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                color = textColor
            )
        }
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(2.dp, colorResource(R.color.gray_25)),
        shape = RoundedCornerShape(16.dp)
    ) {
        val defaultImage = painterResource(R.drawable.profile_character1)
        Image(
            modifier = Modifier
                .size(54.dp)
                .aspectRatio(1f),
            painter = rememberAsyncImagePainter(
                model = imageUrl,
                fallback = defaultImage,
                error = defaultImage,
                placeholder = defaultImage
            ),
            contentScale = ContentScale.Fit,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun ProfileDetailScreenPreview() {
    ProfileDetailScreen(
        profileImage = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832",
        userBlocked = false,
        onClickCancel = {},
        onClickBlock = {},
        onClickFollow = {}
    )
}