package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
                style = FantooChatTypography.subtitle2.copy(color = colorResource(id = R.color.gray_900))
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
        Column(modifier = Modifier.fillMaxHeight()) {
            AddChatSearchItem()
        }
    }
}

@Composable
fun AddChatSearchItem() {
    var text by rememberSaveable { mutableStateOf("") }
    Surface(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 14.dp)
    ) {
        BasicTextField(
            value = text, onValueChange = { text = it },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colorResource(id = R.color.gray_500)
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(6.dp),
                elevation = 0.dp,
                border = BorderStroke(0.5.dp, colorResource(id = R.color.gray_200)),
                backgroundColor = colorResource(id = R.color.gray_25)
            ) {
                Row(
                    modifier = Modifier.padding(end = 12.dp),
                    verticalAlignment = CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_search_gray),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(CenterVertically)
                    )
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.add_chat_search_hint),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = colorResource(id = R.color.gray_500)
                        )
                    } else {
                        it()
                    }
                }
            }
        }
    }
}

@Composable
fun AddChatFollowList() {

}

@Composable()
fun AddChatFanTooList() {

}