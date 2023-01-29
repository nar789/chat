package com.rndeep.fns_fantoo.ui.chatting.chatlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rndeep.fns_fantoo.R

@Composable
fun ChatListLoginError(onClickLogin: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.bg_bg_light_gray_50)
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.character_main2),
                contentDescription = null,
                alignment = Center,
                modifier = Modifier.wrapContentSize()
            )
            Text(
                text = stringResource(id = R.string.se_r_require_login_for_chatting),
                modifier = Modifier.wrapContentSize(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colorResource(id = R.color.gray_600)
            )

            Card(
                elevation = 0.dp,
                backgroundColor = colorResource(id = R.color.primary_default),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 40.dp)
                    .clickable { onClickLogin() },
                shape = RoundedCornerShape(50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .width(120.dp),
                    contentAlignment = Center
                ) {
                    Text(
                        text = stringResource(id = R.string.r_login),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorResource(id = R.color.gray_25)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatListLoginErrorPreview() {
    MaterialTheme {
        ChatListLoginError {}
    }
}