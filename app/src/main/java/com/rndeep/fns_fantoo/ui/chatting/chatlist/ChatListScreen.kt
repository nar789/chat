package com.rndeep.fns_fantoo.ui.chatting.chatlist

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatListResult
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography
import com.rndeep.fns_fantoo.utils.TimeUtils.convertDiffTime
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ChatListScreen(viewModel: ChatListViewModel) {
    Surface(modifier = Modifier) {
        Column {
            ChatListHeader()
            ChatList(viewModel.chatList)
        }
    }
}

@Composable
fun ChatListHeader() {
    Surface(
        Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = colorResource(id = R.color.gray_25)
    ) {
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (title, addBtn) = createRefs()
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(start = 20.dp),
                text = stringResource(R.string.ch_chatting),
                style = FantooChatTypography.subtitle1.copy(color = colorResource(id = R.color.gray_900)),
            )
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(36.dp)
                    .constrainAs(addBtn) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(end = 12.dp)
                    .clickable { Log.d("inhwa", "채팅방으로 이동해야 함") },
                painter = painterResource(R.drawable.outline_icon_outline_plus),
                contentDescription = null
            )
        }
    }
}

@Composable
fun ChatList(chatList: List<ChatListResult>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.bg_bg_light_gray_50)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(chatList) { index, item ->
                Box(
                    modifier = Modifier.padding(
                        top = if (index == 0) 18.dp else 8.dp,
                        bottom = if (index == chatList.lastIndex) 18.dp else 0.dp
                    )
                ) {
                    ChatListItem(chat = item)
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatListResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 20.dp, end = 20.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorResource(id = R.color.gray_25),
        border = BorderStroke(width = 0.5.dp, color = colorResource(id = R.color.gray_200))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            GlideImage(
                modifier = Modifier
                    .size(46.dp, 46.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(16.dp)),
                imageModel = chat.profileImg,
                contentScale = ContentScale.Crop,
                error = ImageBitmap.imageResource(id = R.drawable.character_main2)
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = chat.roomName,
                    style = FantooChatTypography.h3.copy(color = colorResource(id = R.color.gray_700)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    text = chat.chat,
                    style = FantooChatTypography.h4.copy(color = colorResource(id = R.color.gray_400)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 2.dp)
            ) {
                Text(
                    text = chat.time.convertDiffTime(),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = colorResource(
                            id = R.color.gray_300
                        )
                    )
                )
                if (chat.count > 1) {
                    Card(
                        elevation = 0.dp,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .align(alignment = Alignment.End)
                            .wrapContentSize(),
                        backgroundColor = colorResource(id = R.color.primary_100),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text(
                            text = chat.count.toString(),
                            modifier = Modifier
                                .wrapContentSize(align = Alignment.Center)
                                .padding(horizontal = 3.dp, vertical = 2.dp),
                            lineHeight = 18.sp,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.gray_800)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatListItemPreview() {
    MaterialTheme {
        Surface {
            ChatListItem(chat = ChatListResult())
        }
    }
}
//
//@Preview
//@Composable
//fun ChatListScreenPreview() {
//    MaterialTheme {
//        ChatListScreen()
//    }
//}