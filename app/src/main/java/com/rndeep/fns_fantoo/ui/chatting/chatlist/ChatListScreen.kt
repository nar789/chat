package com.rndeep.fns_fantoo.ui.chatting.chatlist

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.chat.ChatListResult
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography
import com.rndeep.fns_fantoo.utils.TimeUtils.convertDiffTime
import com.rndeep.fns_fantoo.utils.toDp
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.roundToInt


private const val ANIMATION_DURATION = 500
private const val MIN_DRAG_AMOUNT = 6
private const val CARD_DRAG_AMOUNT = -162f

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    navigateToChat: (chatId: Long) -> Unit,
    navigateToAddChat: () -> Unit
) {
    val isUser by viewModel.isUser
    Surface(modifier = Modifier) {
        Column {
            ChatListHeader(navigateToAddChat)
            if (isUser) {
                ChatList(viewModel, navigateToChat)
            } else {
                ChatListLoginError(viewModel::navigateToLogin)
            }
        }
    }
}

@Composable
fun ChatListHeader(navigateToAddChat: () -> Unit) {
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
                    .clickable { navigateToAddChat() },
                painter = painterResource(R.drawable.outline_icon_outline_plus),
                contentDescription = null
            )
        }
    }
}

@Composable
fun ChatList(
    viewModel: ChatListViewModel,
    onClickChat: (chatId: Long) -> Unit
) {
    val optionOpenedChatId by viewModel.optionOpenedChatId.collectAsState()
    val chatList = viewModel.chatList

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.bg_bg_light_gray_50)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(chatList) { index, chat ->
                Box(
                    modifier = Modifier.padding(
                        top = if (index == 0) 18.dp else 8.dp,
                        bottom = if (index == chatList.lastIndex) 18.dp else 0.dp
                    )
                ) {
                    ChatListItem(
                        chat = chat,
                        onClickChat = onClickChat,
                        exitChat = viewModel::exitChat,
                        isOptionOpened = optionOpenedChatId == chat.chatId,
                        openOptions = viewModel::openOptions,
                        closeOptions = viewModel::closeOptions
                    )
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: ChatListResult,
    onClickChat: (chatId: Long) -> Unit,
    exitChat: (chatId: Long) -> Unit,
    isOptionOpened: Boolean,
    openOptions: (chatId: Long) -> Unit,
    closeOptions: (chatId: Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 20.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorResource(id = R.color.gray_25),
        border = BorderStroke(width = 0.5.dp, color = colorResource(id = R.color.gray_200))
    ) {
        ChatListEditContent(chat, {}, {}, exitChat)
        ChatListContent(
            chat = chat,
            onClickChat = onClickChat,
            isOpened = isOptionOpened,
            openOptions = openOptions,
            closeOptions = closeOptions
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ChatListContent(
    chat: ChatListResult,
    onClickChat: (chatId: Long) -> Unit,
    isOpened: Boolean,
    openOptions: (Long) -> Unit,
    closeOptions: (Long) -> Unit
) {
    val transitionState = remember {
        MutableTransitionState(isOpened).apply {
            targetState = !isOpened
        }
    }
    val transition = updateTransition(transitionState, "cardTransition")
    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isOpened) CARD_DRAG_AMOUNT.toDp() else 0f })

    Card(elevation = 0.dp,
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetTransition.roundToInt(), 0) }
            .pointerInput(key1 = chat.chatId) {
                detectHorizontalDragGestures { _, dragAmount ->
                    when {
                        dragAmount >= MIN_DRAG_AMOUNT -> closeOptions(chat.chatId)
                        dragAmount < -MIN_DRAG_AMOUNT -> openOptions(chat.chatId)
                    }
                }
            }) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clickable {
                    if (isOpened) return@clickable
                    chat.count = 0
                    onClickChat(chat.chatId)
                }
        ) {
            GlideImage(
                modifier = Modifier
                    .size(46.dp, 46.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(16.dp)),
                imageModel = chat.profileImg,
                failure = {
                    Image(
                        painterResource(R.drawable.profile_character11),
                        contentDescription = null
                    )
                },
                contentScale = ContentScale.Crop
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
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

@Composable
fun ChatListEditContent(
    chat: ChatListResult,
    onClickAlarm: (chatId: Long) -> Unit,
    onClickBlock: (chatId: Long) -> Unit,
    onClickExit: (chatId: Long) -> Unit
) {
    val modifier = Modifier
        .width(54.dp)
        .height(70.dp)

    val chatId = chat.chatId
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = modifier
                .background(color = colorResource(id = R.color.primary_default))
                .clickable { onClickAlarm(chatId) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.chat_list_alarm_on),
                style = TextStyle(
                    color = colorResource(id = R.color.gray_25),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = modifier
                .background(color = colorResource(id = R.color.gray_700))
                .clickable { onClickBlock(chatId) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.chat_list_block),
                style = TextStyle(
                    color = colorResource(id = R.color.gray_25),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = modifier
                .background(color = colorResource(id = R.color.state_danger))
                .clickable { onClickExit(chatId) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.chat_list_exit),
                style = TextStyle(
                    color = colorResource(id = R.color.gray_25),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun ChatListItemPreview() {
    MaterialTheme {
        Surface {
            ChatListItem(chat = ChatListResult(), {}, {}, false, {}, {})
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