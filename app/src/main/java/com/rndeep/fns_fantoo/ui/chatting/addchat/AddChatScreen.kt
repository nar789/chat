package com.rndeep.fns_fantoo.ui.chatting.addchat

import androidx.annotation.StringRes
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
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
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
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.chat.TmpUserInfo
import com.rndeep.fns_fantoo.ui.chatting.compose.FantooChatTypography
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun AddChatScreen(viewModel: AddChatViewModel, onBack: () -> Unit) {
    Surface(modifier = Modifier) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (header, content) = createRefs()
            AddChatHeader(modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top)
            }, onBack)
            AddChatContent(modifier = Modifier.constrainAs(content) {
                top.linkTo(header.bottom)
            }, viewModel)
            if (!viewModel.showEmptyFollow) {
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
fun AddChatHeader(modifier: Modifier, onBack: () -> Unit) {
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
        }
    }
}

@Composable
fun AddChatContent(modifier: Modifier, viewModel: AddChatViewModel) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = colorResource(id = R.color.bg_bg_light_gray_50)
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            AddChatSearchItem(viewModel.searchQuery, viewModel::onQueryInput)
            if (viewModel.showEmptyFollow) {
                AddChatNoFollowerItem()
            } else {
                AddChatList(viewModel)
            }
        }
    }
}

@Composable
fun AddChatList(viewModel: AddChatViewModel) {
    val followList = viewModel.followList
    val fantooList = viewModel.fantooList
    val checkedIds = viewModel.checkedUserList

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.gray_25))
    ) {
        if (followList.isNotEmpty()) {
            itemsIndexed(followList) { index, user ->
                if (index == 0) {
                    AddChatTitleItem(
                        titleResId = R.string.add_chat_follow_list,
                        count = if (viewModel.searchQuery.value.isEmpty()) followList.size else null
                    )
                }
                Box(
                    modifier = Modifier.padding(
                        top = if (index == 0) 8.dp else 6.dp,
                        bottom = when (index) {
                            followList.lastIndex -> {
                                if (fantooList.isEmpty()) 92.dp else 22.dp
                            }
                            else -> 6.dp
                        }
                    )
                ) {
                    FollowerItem(
                        userInfo = user,
                        isChecked = checkedIds.contains(user.loginId),
                        viewModel::onCheckStateChanged
                    )
                }
            }
        }

        if (fantooList.isNotEmpty()) {
            itemsIndexed(fantooList) { index, user ->
                if (index == 0) {
                    if (followList.isNotEmpty()) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(
                                    color = colorResource(id = R.color.bg_bg_light_gray_50)
                                )
                        )
                    }
                    AddChatTitleItem(titleResId = R.string.add_chat_fantoo_list)
                }

                Box(
                    modifier = Modifier.padding(
                        top = if (index == 0) 8.dp else 6.dp,
                        bottom = if (index == fantooList.lastIndex) 92.dp else 6.dp
                    )
                ) {
                    FollowerItem(
                        userInfo = user,
                        isChecked = checkedIds.contains(user.loginId),
                        onClick = viewModel::onCheckStateChanged
                    )
                }
            }
        }
    }
}

@Composable
fun FollowerItem(userInfo: TmpUserInfo, isChecked: Boolean, onClick: (id: String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 18.dp)
            .clickable {
                onClick(userInfo.loginId)
            }
    ) {
        GlideImage(
            imageModel = userInfo.userPhoto,
            modifier = Modifier
                .size(38.dp)
                .align(CenterVertically)
                .clip(RoundedCornerShape(12.dp)),
            failure = {
                Image(painterResource(R.drawable.profile_character11), contentDescription = null)
            },
            contentScale = ContentScale.Crop
        )

        Text(
            text = userInfo.userName,
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
fun AddChatSearchItem(query: State<String>, updateQuery: (query: String) -> Unit) {
    val queryText by query
    Surface(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 14.dp)
    ) {
        BasicTextField(
            value = queryText, onValueChange = { updateQuery(it) },
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
                        if (queryText.isEmpty()) {
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