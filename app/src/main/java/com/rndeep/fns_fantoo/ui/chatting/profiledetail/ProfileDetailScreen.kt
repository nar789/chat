package com.rndeep.fns_fantoo.ui.chatting.profiledetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileDetailScreen(
    profileImage: String
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        ProfileDetailContent(
            modifier = Modifier.padding(top = 27.dp),
            userName = "Dasol",
            onClickCancel = {}
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
    onClickCancel: () -> Unit
) {
    Surface(
        modifier = modifier
            .width(276.dp)
            .height(192.dp),
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
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                IconSelector(
                    modifier = Modifier,
                    imageSource = painterResource(R.drawable.icon_outline_addfriend),
                    buttonName = stringResource(R.string.profile_detail_follow_btn),
                    onClick = {}
                )
                IconSelector(
                    modifier = Modifier,
                    imageSource = painterResource(R.drawable.icon_outline_blockaccount),
                    buttonName = stringResource(R.string.profile_detail_block_btn),
                    onClick = {}
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
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(start = 34.dp, end = 34.dp, top = 14.dp)
                .size(32.dp),
            painter = imageSource,
            contentDescription = null
        )
        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.primary_500)
            ),
            border = BorderStroke(1.dp, colorResource(R.color.primary_500)),
            contentPadding = PaddingValues(3.dp, 5.dp)
        ) {
            Text(
                text = buttonName,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.state_enable_gray_25)
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
        GlideImage(
            modifier = Modifier
                .size(54.dp)
                .aspectRatio(1f),
            imageModel = imageUrl,
            previewPlaceholder = R.drawable.profile_character1,
            failure = {
                Image(
                    modifier = Modifier.size(54.dp),
                    painter = painterResource(R.drawable.profile_character1),
                    contentDescription = null)
            },
            contentScale = ContentScale.Fit
        )
    }
}

@Preview
@Composable
fun ProfileDetailScreenPreview() {
    ProfileDetailScreen(
        profileImage = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832"
    )
}