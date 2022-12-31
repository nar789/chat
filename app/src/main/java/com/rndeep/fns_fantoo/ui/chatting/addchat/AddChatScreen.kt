package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.GetUserListResponse
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography
import com.rndeep.fns_fantoo.ui.chatting.compose.getImageUrlFromCDN

@Composable
fun AddChatScreen(viewModel: AddChatViewModel, onBack: () -> Unit) {
    Surface(modifier = Modifier) {
        val followList = viewModel.followList.collectAsLazyPagingItems()

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (header, content) = createRefs()
            AddChatHeader(modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top)
            }, onBack, viewModel::makeTmpChatRoom)

            val query by viewModel.searchQuery.collectAsState("")
            AddChatContent(
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(header.bottom)
                }, followList = followList,
                searchList = viewModel.searchList.collectAsLazyPagingItems(),
                checkedUserList = viewModel.checkedUserList,
                onCheckStateChanged = viewModel::onCheckStateChanged,
                query = query,
                onQueryInput = viewModel::updateQuery
            )

            if (followList.itemCount > 0) {
                val startBtn = createRef()
                AddChatStartBtn(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .constrainAs(startBtn) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    active = viewModel.checkedUserList.isNotEmpty(),
                    onClick = viewModel::onClickChatStart
                )
            }
        }

    }
}

@Composable
fun AddChatStartBtn(modifier: Modifier, active: Boolean, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = 0.dp,
        shape = RoundedCornerShape(100.dp),
        backgroundColor = colorResource(id = if (active) R.color.primary_default else R.color.gray_200)
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
fun AddChatHeader(modifier: Modifier, onBack: () -> Unit, makeTmpChatRoom: () -> Unit) {
    Surface(
        modifier
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
                alignment = Center
            )

            Text(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(CenterVertically),
                text = stringResource(id = R.string.add_chat_title),
                style = FantooChatTypography.subtitle2.copy(color = colorResource(id = R.color.gray_900))
            )

            //todo 서버 연결 완셩되면 지울 것
            Text(
                modifier = Modifier
                    .padding(start = 30.dp)
                    .align(CenterVertically)
                    .clickable { makeTmpChatRoom() },
                text = "임시 채팅방",
                style = FantooChatTypography.subtitle2.copy(color = colorResource(id = R.color.gray_900))
            )
        }
    }
}

@Composable
fun AddChatContent(
    modifier: Modifier,
    followList: LazyPagingItems<GetUserListResponse.ChatUserDto>,
    searchList: LazyPagingItems<GetUserListResponse.ChatUserDto>?,
    checkedUserList: List<GetUserListResponse.ChatUserDto>,
    onCheckStateChanged: (user: GetUserListResponse.ChatUserDto) -> Unit,
    query: String,
    onQueryInput: (query: String) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = colorResource(id = R.color.gray_25)
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            AddChatSearchItem(query, onQueryInput)
            val isSearch = query.isNotBlank()
            if (isSearch.not() && followList.itemCount == 0 || isSearch && (searchList == null || searchList.itemCount == 0)) {
                AddChatNoFollowerItem()
            } else {
                if (query.isBlank()) {
                    AddChatList(
                        followList,
                        checkedUserList,
                        isSearch,
                        onCheckStateChanged
                    )
                } else {
                    AddChatList(
                        list = searchList ?: return@Column,
                        checkedUserList = checkedUserList,
                        isSearch = isSearch,
                        onCheckStateChanged = onCheckStateChanged
                    )
                }
            }
        }
    }
}

@Composable
fun AddChatList(
    list: LazyPagingItems<GetUserListResponse.ChatUserDto>,
    checkedUserList: List<GetUserListResponse.ChatUserDto>,
    isSearch: Boolean,
    onCheckStateChanged: (user: GetUserListResponse.ChatUserDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.gray_25))
    ) {
        if (list.itemCount > 0) {
            items(count = list.itemCount) { index ->
                val user = list[index] ?: return@items
                if (index == 0) {
                    AddChatTitleItem(
                        titleResId = R.string.add_chat_follow_list,
                        count = if (isSearch.not()) list.itemCount else null
                    )
                }
                Box(
                    modifier = Modifier.padding(
                        top = if (index == 0) 8.dp else 6.dp,
                        bottom = when (index) {
                            list.itemCount -> 140.dp
                            else -> 6.dp
                        }
                    )
                ) {
                    FollowerItem(
                        userInfo = user,
                        isChecked = checkedUserList.contains(user),
                        onClick = onCheckStateChanged
                    )
                }
            }
        }
    }
}

@Composable
fun FollowerItem(
    userInfo: GetUserListResponse.ChatUserDto,
    isChecked: Boolean,
    onClick: (user: GetUserListResponse.ChatUserDto) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 18.dp)
            .clickable {
                onClick(userInfo)
            }
    ) {
        val defaultImage = painterResource(R.drawable.profile_character11)
        Image(
            painter = rememberAsyncImagePainter(
                model = userInfo.userPhoto.getImageUrlFromCDN(),
                error = defaultImage,
                fallback = defaultImage,
                placeholder = defaultImage
            ),
            modifier = Modifier
                .size(38.dp)
                .align(CenterVertically)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Text(
            text = userInfo.userNick,
            modifier = Modifier
                .align(CenterVertically)
                .weight(1f)
                .padding(horizontal = 9.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colorResource(id = R.color.gray_700)
        )

        Box(
            Modifier
                .align(CenterVertically)
                .background(
                    color = colorResource(
                        id = if (isChecked) R.color.primary_default else R.color.gray_200
                    ),
                    shape = CircleShape
                )
                .size(20.dp),
            contentAlignment = Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_check_white),
                contentDescription = null
            )
        }
    }
}

@Composable
fun AddChatSearchItem(query: String, updateQuery: (query: String) -> Unit) {
    Surface(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.bg_bg_light_gray_50))
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 14.dp)
    ) {
        BasicTextField(
            value = query, onValueChange = { updateQuery(it) },
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
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.add_chat_search_hint),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = colorResource(id = R.color.gray_500)
                            )
                        }
                        it()
                    }
                }
            }
        }
    }
}

@Composable
fun AddChatTitleItem(@StringRes titleResId: Int, count: Int? = null) {
    Surface(
        Modifier
            .background(color = colorResource(id = R.color.gray_25))
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = stringResource(id = titleResId) + if (count == null) "" else " $count",
            modifier = Modifier.padding(top = 18.dp, start = 20.dp),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = colorResource(id = R.color.gray_600)
        )
    }
}

@Composable
fun AddChatNoFollowerItem() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.gray_25))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.character_main2),
                contentDescription = null,
                modifier = Modifier.padding(top = 150.dp)
            )

            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(id = R.string.add_chat_no_follow),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colorResource(id = R.color.gray_800)
            )
        }
    }
}