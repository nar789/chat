package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.layout.ContentScale
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.chat.Message
import com.rndeep.fns_fantoo.data.remote.model.chat.ReadInfo
import com.rndeep.fns_fantoo.ui.chatting.chat.model.ChatUiState
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography
import com.rndeep.fns_fantoo.ui.chatting.compose.getChatImageUrl
import com.rndeep.fns_fantoo.ui.chatting.compose.getImageUrlFromCDN
import com.skydoves.landscapist.rememberDrawablePainter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@Composable
fun ChattingScreen(
    uiState: ChatUiState,
    modifier: Modifier = Modifier,
    titleText: String,
    onMessageSent: (String) -> Unit,
    onTranslateClicked: () -> Unit,
    onImageClicked: (String) -> Unit,
    onImageSelectorClicked: () -> Unit,
    onClickUnBlock: () -> Unit,
    onClickAuthor: (String) -> Unit,
    onClickMore: () -> Unit,
    onBack: () -> Unit
) {
    val messageList = uiState.messages.collectAsLazyPagingItems()

    // LazyColumn scroll state
    val scrollState = rememberLazyListState()

    // last visible item state
    val lastVisibleItemIndex by remember {
        derivedStateOf { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
    }

    // The coroutine scope for event handlers calling suspend functions.
    val coroutineScope = rememberCoroutineScope()
    var jobState: Job? by remember { mutableStateOf(null) }

    // True if floating date text is shown.
    var floatingDateShown by remember { mutableStateOf(false) }

    suspend fun hideFloatingDate() {
        if (floatingDateShown) {
            delay(500L)
            floatingDateShown = false
        }
    }

    Surface(
        modifier = modifier,
        color = colorResource(R.color.gray_50)
    ) {
        Column {
            ChatHeader(
                titleText,
                isTranslateModeOn = uiState.translateMode,
                onTranslateClicked = onTranslateClicked,
                onClickMore = onClickMore,
                onBack = onBack
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(colorResource(R.color.gray_50))
            ) {
                Messages(
                    messages = messageList,
                    modifier = Modifier.fillMaxWidth(),
                    myId = uiState.myId,
                    onImageClicked = onImageClicked,
                    onClickAuthor = onClickAuthor,
                    scrollState = scrollState,
                    translateMode = uiState.translateMode,
                    readInfos = uiState.readInfos,
                    userCount = uiState.userCount
                )

                FloatingDateText(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(0.dp, 14.dp),
                    shown = floatingDateShown,
                    date = messageList.getOrNull(lastVisibleItemIndex)?.dateText.orEmpty()
                )
            }

            if (uiState.blocked) {
                UserBlockView(onClickUnBlock = onClickUnBlock)
            } else {
                BottomEditText(
                    onMessageSent,
                    onImageSelectorClicked,
                    scrollToBottom = { scrollState.scrollToItem(0) }
                )
            }

            val snapshotMessages = messageList.itemSnapshotList
            // scroll to bottom
            LaunchedEffect(snapshotMessages) {
                val isScrolledToEnd = scrollState.firstVisibleItemIndex <= 1
                val lastMessageIsMine =
                    snapshotMessages.firstOrNull()?.isMyMessage(uiState.myId) == true
                if (isScrolledToEnd || lastMessageIsMine) {
                    scrollState.scrollToItem(0)
                }
            }

            // floating date text visibility animation
            if (scrollState.isScrollInProgress) {
                DisposableEffect(Unit) {
                    jobState?.cancel()
                    if (snapshotMessages.isNotEmpty() && !floatingDateShown) floatingDateShown =
                        true
                    onDispose {
                        jobState = coroutineScope.launch { hideFloatingDate() }
                    }
                }
            }
        }
    }
}

private fun <T : Any> LazyPagingItems<T>.getOrNull(index: Int): T? {
    return if ((itemCount == 0) || (index !in (0 until itemCount))) {
        null
    } else {
        get(index)
    }
}

@Composable
fun Messages(
    messages: LazyPagingItems<Message>,
    modifier: Modifier,
    scrollState: LazyListState,
    myId: String,
    readInfos: List<ReadInfo>,
    userCount: Int,
    translateMode: Boolean,
    onImageClicked: (String) -> Unit,
    onClickAuthor: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = scrollState,
        reverseLayout = true
    ) {
        val lastIndex = messages.itemCount - 1
        items(messages.itemCount) { index ->
            val item = messages[index] ?: return@items
            val isMe = item.isMyMessage(myId)
            val lastItem = index == lastIndex

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = if (lastItem) 18.dp else 0.dp),
                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                if (item.messageType == 3) {
                    ExitMessageItem(message = item, translateMode = translateMode)
                } else {
                    val prevItem = messages.getOrNull(index + 1)
                    val nextItem = messages.getOrNull(index - 1)
                    val prevAuthor = prevItem?.userId
                    val nextAuthor = nextItem?.userId
                    val nextHour = nextItem?.hourText
                    val isFirstMessageByAuthor =
                        prevAuthor != item.userId || prevItem?.isNormalType == false
                    val isLastMessageByAuthor =
                        nextAuthor != item.userId || nextItem?.isNormalType == false
                    MessageItem(
                        message = item,
                        isMe = isMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor,
                        timestampVisible = nextHour != item.hourText || isLastMessageByAuthor,
                        onImageClicked = onImageClicked,
                        onClickAuthor = onClickAuthor,
                        readInfos = readInfos,
                        userCount = userCount,
                        translateMode = translateMode
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
    readInfos: List<ReadInfo>,
    translateMode: Boolean,
    onImageClicked: (String) -> Unit,
    onClickAuthor: (String) -> Unit,
    userCount: Int
) {
    Column {
        if (isFirstMessageByAuthor && !isMe) {
            AuthorAndName(message = message, onClickAuthor = onClickAuthor)
            Spacer(modifier = Modifier.height(1.dp))
        }

        Row(verticalAlignment = Alignment.Bottom) {
            if (!isMe) Spacer(modifier = Modifier.width(24.dp))

            if (isMe) {
                TimestampAndUnreadCount(
                    message = message,
                    isMe = true,
                    timestampVisible = timestampVisible,
                    readInfos = readInfos,
                    userCount = userCount
                )
            }

            if (message.image.isNullOrEmpty()) {
                TextMessageItem(message = message, isMe = isMe, translateMode = translateMode)
            } else {
                ImageMessageItem(message = message, onImageClicked = onImageClicked)
            }

            if (!isMe) {
                TimestampAndUnreadCount(
                    message = message,
                    isMe = false,
                    timestampVisible = timestampVisible,
                    readInfos = readInfos,
                    userCount = userCount
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
    message: Message,
    onClickAuthor: (String) -> Unit
) {
    Row(
        modifier = Modifier.clickable(
            interactionSource = MutableInteractionSource(),
            indication = rememberRipple(bounded = false)
        ) { message.userId?.let(onClickAuthor) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val defaultImage = painterResource(R.drawable.profile_character1)
        Image(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp)),
            painter = rememberAsyncImagePainter(
                model = message.userPhoto.getImageUrlFromCDN(),
                fallback = defaultImage,
                error = defaultImage,
                placeholder = defaultImage
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = message.displayName.orEmpty(),
            fontSize = 12.sp,
            color = colorResource(R.color.gray_400),
        )
    }
}

@Composable
fun TextMessageItem(
    message: Message,
    isMe: Boolean,
    translateMode: Boolean
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
            text = if (translateMode) message.translatedMessage.orEmpty() else message.message,
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
    val defaultImage = painterResource(id = R.drawable.character_main2)
    Image(
        modifier = Modifier
            .clickable { onImageClicked(message.image.orEmpty()) }
            .padding(4.dp)
            .widthIn(max = 149.dp)
            .clip(RoundedCornerShape(12.dp)),
        painter = rememberAsyncImagePainter(
            model = message.image.getChatImageUrl(),
            fallback = defaultImage, error = defaultImage, placeholder = defaultImage
        ),
        contentDescription = null
    )
}

@Composable
fun ExitMessageItem(
    message: Message,
    translateMode: Boolean
) {
    val text = if (translateMode) {
        message.translatedMessage
    } else {
        message.message
    }.let {
        String.format(it.replace("％s", "%s"), message.displayName)
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        text = text,
        color = colorResource(R.color.state_enable_gray_400),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun TimestampAndUnreadCount(
    message: Message,
    isMe: Boolean,
    timestampVisible: Boolean,
    readInfos: List<ReadInfo>,
    userCount: Int
) {
    Column(
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        val unReadCount = message.getUnReadCount(readInfos, userCount)
        if (unReadCount > 0) {
            Text(
                text = "$unReadCount",
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
    onImageSelectorClicked: () -> Unit,
    scrollToBottom: suspend () -> Unit
) {
    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var focusState by remember { mutableStateOf(false) }
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var inputActivateMode by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                    focusRequester = focusRequester,
                    onClick = { scope.launch { scrollToBottom() } }
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                    scrollToBottom()
                }
            }
            UserInputSelector(
                userInputActivated = userInputActivated,
                sendBtnEnabled = textState.text.isNotEmpty(),
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
    focusRequester: FocusRequester,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }

    if (source.collectIsPressedAsState().value) onClick()

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
            interactionSource = source
        )
    }
}

@Composable
fun UserInputSelector(
    userInputActivated: Boolean,
    sendBtnEnabled: Boolean,
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
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(bounded = false)
                ) { onImageSelectorClicked() }
                .size(32.dp)
                .padding(4.dp),
            painter = painterResource(R.drawable.icon_outline_picture),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorResource(R.color.state_active_gray_700))
        )

        if (userInputActivated) {
            SendButton(sendBtnEnabled, onMessageSent)
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
    enabled: Boolean,
    onSendClicked: () -> Unit,
) {
    val bgColor =
        colorResource(if (enabled) R.color.primary_300 else R.color.state_disabled_gray_200)

    IconButton(
        modifier = Modifier
            .background(bgColor, shape = CircleShape)
            .size(32.dp),
        onClick = { onSendClicked() },
        enabled = enabled
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
    onClickMore: () -> Unit,
    onBack: () -> Unit
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
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(bounded = false)
                    ) { onBack() }
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
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(bounded = false)
                    ) { onTranslateClicked() }
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
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(bounded = false)
                    ) { onClickMore() }
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
fun FloatingDateText(
    modifier: Modifier = Modifier,
    shown: Boolean,
    date: String,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = shown,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 150)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
        )
    ) {
        Surface(
            modifier = Modifier
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
}

@Composable
fun UserBlockView(
    modifier: Modifier = Modifier,
    onClickUnBlock: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(120.dp),
        color = colorResource(R.color.state_enable_gray_25),
        border = BorderStroke((0.5).dp, Color(0x1e000000))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 49.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.chatting_block_description),
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
                ),
                shape = RoundedCornerShape(100.dp),
                elevation = null
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
            ChatUiState(messages = emptyFlow()),
            Modifier,
            "Dasol",
            onMessageSent = {},
            onTranslateClicked = {},
            onImageClicked = {},
            onImageSelectorClicked = {},
            onClickMore = {},
            onClickAuthor = {},
            onClickUnBlock = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
fun BottomEditTextPreview() {
    MaterialTheme {
        BottomEditText({}, {}) {}
    }
}

@Preview
@Composable
fun UserInputSelector() {
    MaterialTheme {
        Column {
            Surface {
                UserInputSelector(userInputActivated = true, true, {}, {}) {}
            }
            Spacer(Modifier.size(15.dp))
            Surface {
                UserInputSelector(userInputActivated = false, true, {}, {}) {}
            }
        }
    }
}

@Preview
@Composable
fun UserBlockViewPreview() {
    UserBlockView(Modifier, {})
}