package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography

@Composable
fun AddChatScreen(onBack: () -> Unit) {
    Surface(modifier = Modifier) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                AddChatHeader(onBack)
                AddChat()
            }
            val startBtn = createRef()
            AddChatStartBtn(modifier = Modifier
                .padding(bottom = 20.dp)
                .constrainAs(startBtn) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
        }

    }
}

@Composable
fun AddChatStartBtn(modifier: Modifier) {
    val isAvailable = false
    Card(
        modifier = modifier,
        elevation = 0.dp,
        shape = RoundedCornerShape(100.dp),
        backgroundColor = colorResource(id = if (isAvailable) R.color.primary_default else R.color.gray_200)
    ) {
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .height(42.dp)
                .width(320.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_chat_start),
                color = colorResource(id = R.color.gray_25),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AddChatHeader(onBack: () -> Unit) {
    Surface(
        Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = colorResource(id = R.color.gray_25)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_outline_back),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 14.dp)
                    .align(CenterVertically)
                    .clickable { onBack() },
                alignment = Alignment.Center
            )

            Text(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(CenterVertically),
                text = stringResource(id = R.string.add_chat_title),
                style = FantooChatTypography.subtitle1.copy(color = colorResource(id = R.color.gray_900))
            )
        }
    }
}

@Composable
fun AddChat() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.bg_bg_light_gray_50)
    ) {

    }
}

@Composable
fun AddChatSearchItem() {

}

@Composable
fun AddChatFollowList() {

}

@Composable()
fun AddChatFanTooList() {

}