package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rndeep.fns_fantoo.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ChattingScreen(
    uiState: ChatUiState,
    modifier: Modifier = Modifier
) {
    val messageList = uiState.messages

    Surface(modifier = modifier) {
        Messages(messageList)
    }
}

@Composable
fun Messages(
    messages: List<Message>
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(R.color.gray_50)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(messages) { index, item ->
                val isMe = item.authorName == "Me"
                val prevAuthor = messages.getOrNull(index - 1)?.authorName
                val nextAuthor = messages.getOrNull(index + 1)?.authorName
                val nextDate = messages.getOrNull(index + 1)?.dateText
                val isFirstMessageByAuthor = prevAuthor != item.authorName
                val isLastMessageByAuthor = nextAuthor != item.authorName


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = if (index == 0) 18.dp else 0.dp),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    MessageItem(
                        message = item,
                        isMe = isMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor,
                        timestampVisible = nextDate != item.dateText || isLastMessageByAuthor
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    isMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    timestampVisible: Boolean
) {
    Column {
        if (isFirstMessageByAuthor && !isMe) {
            AuthorAndName(message = message)
            Spacer(modifier = Modifier.height(1.dp))
        }

        Row(verticalAlignment = Alignment.Bottom) {
            if (!isMe) Spacer(modifier = Modifier.width(24.dp))

            if (isMe) {
                TimestampAndUnreadCount(
                    message = message,
                    isMe = true,
                    timestampVisible = timestampVisible
                )
            }

            if (message.image.isNullOrEmpty()) {
                TextMessageItem(message = message, isMe = isMe)
            } else {
                ImageMessageItem(message = message)
            }

            if (!isMe) {
                TimestampAndUnreadCount(
                    message = message,
                    isMe = false,
                    timestampVisible = timestampVisible
                )
            }
        }

        if (isLastMessageByAuthor) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AuthorAndName(
    message: Message
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        GlideImage(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp)),
            imageModel = message.authorImage ?: R.drawable.profile_character1,
            previewPlaceholder = R.drawable.profile_character1,
            failure = {
                Image(painterResource(R.drawable.profile_character1), contentDescription = null)
            }
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = message.authorName,
            fontSize = 12.sp,
            color = colorResource(R.color.gray_400),
        )
    }
}

@Composable
fun TextMessageItem(
    message: Message,
    isMe: Boolean
) {
    val (bgColor, textColor) = if (isMe) {
        colorResource(R.color.gray_700) to colorResource(R.color.gray_25)
    } else {
        colorResource(R.color.gray_25) to colorResource(R.color.gray_900)
    }
    val shape = if (isMe) {
        RoundedCornerShape(14.dp, 2.dp, 14.dp, 14.dp)
    } else {
        RoundedCornerShape(2.dp, 14.dp, 14.dp, 14.dp)
    }
    val border = if (isMe) null else BorderStroke(0.5.dp, colorResource(R.color.gray_200))

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .padding(4.dp)
            .widthIn(max = 254.dp),
        color = bgColor,
        shape = shape,
        elevation = 1.dp,
        border = border
    ) {
        Text(
            text = message.content,
            color = textColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ImageMessageItem(
    message: Message
) {
    GlideImage(
        modifier = Modifier
            .padding(4.dp)
            .widthIn(max = 149.dp)
            .clip(RoundedCornerShape(12.dp)),
        imageModel = message.image,
        previewPlaceholder = R.drawable.character_main2,
        failure = { Text("image load failed") }
    )
}

@Composable
fun TimestampAndUnreadCount(
    message: Message,
    isMe: Boolean,
    timestampVisible: Boolean
) {
    Column(
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        if (message.unreadCount > 0) {
            Text(
                text = "${message.unreadCount}",
                color = colorResource(R.color.primary_500),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                maxLines = 1
            )
        }
        if (timestampVisible) {
            Text(
                text = message.dateText,
                color = colorResource(R.color.gray_400),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun AuthAndNamePreview() {
    val message = Message(
        "당연히 같이 가야지~ 스탠딩 공연이잖아 너무 재밌을것 같어~",
        "Dasol"
    )
    MaterialTheme {
        AuthorAndName(message = message)
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    MaterialTheme {
        ChattingScreen(testUiState, Modifier)
    }
}