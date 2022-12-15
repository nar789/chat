package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.rememberDrawablePainter

@Composable
fun ChattingScreen(
    uiState: ChatUiState,
    modifier: Modifier = Modifier,
    titleText: String,
    userBlocked: Boolean = false,
    onMessageSent: (String) -> Unit,
    onTranslateClicked: () -> Unit,
    onImageClicked: (String) -> Unit,
    onImageSelectorClicked: () -> Unit,
    onClickUnBlock: () -> Unit = {}
) {
    val messageList = uiState.messages

    Surface(
        modifier = modifier,
        color = colorResource(R.color.gray_50)
    ) {
        Column {
            ChatHeader(
                titleText,
                isTranslateModeOn = uiState.translateMode,
                onTranslateClicked = onTranslateClicked
            )
            Box(modifier = Modifier.weight(1f)) {
                Messages(
                    messages = messageList,
                    modifier = Modifier.fillMaxSize(),
                    onImageClicked = onImageClicked
                )
                DateFloatingText(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(0.dp, 14.dp),
                    enabled = true,
                    date = "2011.11.11"
                )
            }

            if (userBlocked) {
                UserBlockView(onClickUnBlock = onClickUnBlock)
            } else {
                BottomEditText(
                    onMessageSent,
                    onImageSelectorClicked
                )
            }
        }
    }
}

@Composable
fun Messages(
    messages: List<Message>,
    modifier: Modifier,
    onImageClicked: (String) -> Unit
) {
    Surface(
        modifier = modifier,
        color = colorResource(R.color.gray_50)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(messages) { index, item ->
                val isMe = item.authorName == "Me"
                val prevAuthor = messages.getOrNull(index - 1)?.authorName
                val nextAuthor = messages.getOrNull(index + 1)?.authorName
                val nextHour = messages.getOrNull(index + 1)?.hourText
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
                        timestampVisible = nextHour != item.hourText || isLastMessageByAuthor,
                        onImageClicked = onImageClicked
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
    timestampVisible: Boolean,
    onImageClicked: (String) -> Unit
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
                ImageMessageItem(message = message, onImageClicked = onImageClicked)
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
    message: Message,
    onImageClicked: (String) -> Unit
) {
    GlideImage(
        modifier = Modifier
            .clickable { onImageClicked(message.image.orEmpty()) }
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
                text = message.hourText,
                color = colorResource(R.color.gray_400),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                maxLines = 1
            )
        }
    }
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@Composable
fun BottomEditText(
    onMessageSent: (String) -> Unit,
    onImageSelectorClicked: () -> Unit
) {
    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var focusState by remember { mutableStateOf(false) }
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var inputActivateMode by remember { mutableStateOf(false) }

    val userInputActivated = inputActivateMode || textState.text.isNotEmpty()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colorResource(R.color.gray_25)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .semantics {
                    keyboardShownProperty = focusState
                }
        ) {
            if (userInputActivated) {
                UserInputText(
                    textFieldValue = textState,
                    onTextChanged = { textState = it },
                    showKeyboard = focusState,
                    onFocusChanged = { focusState = it },
                    focusRequester = focusRequester
                )

                LaunchedEffect(Unit) { focusRequester.requestFocus() }
            }
            UserInputSelector(
                userInputActivated = userInputActivated,
                onMessageSent = {
                    onMessageSent(textState.text)
                    textState = TextFieldValue()
                },
                onUserInputActivate = { inputActivateMode = true },
                onImageSelectorClicked = onImageSelectorClicked
            )
        }
    }
}

@Composable
fun UserInputText(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    showKeyboard: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    focusRequester: FocusRequester
) {
    Surface(Modifier.semantics {
        keyboardShownProperty = showKeyboard
    }) {
        var lastFocusState by remember { mutableStateOf(false) }
        BasicTextField(
            value = textFieldValue,
            onValueChange = onTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (lastFocusState != it.isFocused) {
                        onFocusChanged(it.isFocused)
                    }
                    lastFocusState = it.isFocused
                },
            maxLines = 4,
            cursorBrush = SolidColor(LocalContentColor.current),
            textStyle = TextStyle(
                color = colorResource(R.color.gray_800),
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
    }
}

@Composable
fun UserInputSelector(
    userInputActivated: Boolean,
    onMessageSent: () -> Unit,
    onUserInputActivate: () -> Unit,
    onImageSelectorClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .clickable { onImageSelectorClicked() }
                .size(32.dp)
                .padding(4.dp),
            painter = painterResource(R.drawable.icon_outline_picture),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorResource(R.color.state_active_gray_700))
        )

        if (userInputActivated) {
            SendButton(onMessageSent)
        } else {
            Text(
                modifier = Modifier
                    .clickable { onUserInputActivate() }
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .weight(1f),
                text = stringResource(R.string.chatting_edit_text_hint),
                color = colorResource(R.color.state_enable_gray_400),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SendButton(
    onSendClicked: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .background(colorResource(R.color.primary_300), shape = CircleShape)
            .size(32.dp),
        onClick = onSendClicked
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            painter = painterResource(R.drawable.icon_outline_send),
            contentDescription = null,
            tint = colorResource(R.color.state_enable_gray_25)
        )
    }
}

@Composable
fun ChatHeader(
    titleText: String,
    isTranslateModeOn: Boolean,
    onTranslateClicked: () -> Unit,
) {
    Surface(
        Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = colorResource(id = R.color.gray_25)
    ) {
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (backBtn, title, translateBtn, moreBtn) = createRefs()
            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { }
                    .padding(4.dp)
                    .constrainAs(backBtn) {
                        start.linkTo(parent.start, margin = 10.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(title.start)
                    },
                painter = painterResource(R.drawable.icon_outline_back),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(backBtn.end, margin = 15.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(translateBtn.start, margin = 9.dp)
                        width = Dimension.fillToConstraints
                    },
                text = titleText,
                style = FantooChatTypography.subtitle2.copy(color = colorResource(id = R.color.gray_900)),
                lineHeight = 24.sp
            )

            val translateBtnColorFilter = when {
                isTranslateModeOn -> ColorFilter.tint(colorResource(R.color.primary_500))
                else -> null
            }
            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onTranslateClicked() }
                    .padding(5.dp)
                    .constrainAs(translateBtn) {
                        start.linkTo(title.end)
                        end.linkTo(moreBtn.start, margin = 12.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                painter = painterResource(R.drawable.icon_outline_translate1),
                contentDescription = null,
                colorFilter = translateBtnColorFilter
            )
            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { }
                    .padding(5.dp)
                    .constrainAs(moreBtn) {
                        start.linkTo(translateBtn.end)
                        end.linkTo(parent.end, margin = 12.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                painter = painterResource(R.drawable.icon_outline_more),
                contentDescription = null
            )
        }
    }
}

@Composable
fun DateFloatingText(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    date: String,
) {
    Surface(
        modifier = modifier
            .sizeIn(minWidth = 86.dp, minHeight = 24.dp)
            .paint(
                painter = rememberDrawablePainter(
                    ContextCompat.getDrawable(LocalContext.current, R.drawable.bg_chatting_date)
                )
            )
            .padding(top = 1.dp, bottom = 3.dp, start = 7.dp, end = 7.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = date,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.gray_25)
            )
        }
    }
}

@Composable
fun UserBlockView(
    modifier: Modifier = Modifier,
    onClickUnBlock: () -> Unit
) {
    Surface(
        modifier = modifier.size(360.dp, height = 120.dp),
        color = colorResource(R.color.state_enable_gray_25),
        border = BorderStroke((0.5).dp, Color(0x1e000000))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 49.dp),
                text = String.format(stringResource(R.string.chatting_block_description), "Dasol"),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = colorResource(R.color.gray_400),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(14.dp))
            Button(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .height(36.dp),
                onClick = onClickUnBlock,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.state_enable_gray_400)
                )
            ) {
                Text(
                    text = stringResource(R.string.chatting_unblock_btn),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = colorResource(R.color.gray_25)
                )
            }
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    MaterialTheme {
        ChattingScreen(
            testUiState, Modifier,
            "Dasol",
            onMessageSent = {},
            onTranslateClicked = {},
            onImageClicked = {},
            onImageSelectorClicked = {}
        )
    }
}

@Preview
@Composable
fun BottomEditTextPreview() {
    MaterialTheme {
        BottomEditText({}, {})
    }
}

@Preview
@Composable
fun UserInputSelector() {
    MaterialTheme {
        Column {
            Surface {
                UserInputSelector(userInputActivated = true, {}, {}, {})
            }
            Spacer(Modifier.size(15.dp))
            Surface {
                UserInputSelector(userInputActivated = false, {}, {}, {})
            }
        }
    }
}

@Preview
@Composable
fun UserBlockViewPreview() {
    UserBlockView(Modifier, {})
}