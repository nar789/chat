package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rndeep.fns_fantoo.R

@Composable
fun ChattingSettingScreen() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colorResource(R.color.gray_25),
        shape = RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 32.dp, end = 32.dp, bottom = 38.dp)
        ) {
            IconMove(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.size(10.dp))
            ChattingSettingContent(modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
fun IconMove(
    modifier: Modifier
) {
    Image(
        modifier = modifier.size(50.dp, 4.dp),
        painter = painterResource(R.drawable.icon_move),
        contentDescription = null,
        colorFilter = ColorFilter.tint(colorResource(R.color.gray_100)),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun ChattingSettingContent(
    modifier: Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val settingItemModifier = Modifier
            .fillMaxWidth()
            .heightIn(34.dp)

        var onOff by remember { mutableStateOf(true) }
        val notiText = if (onOff) {
            stringResource(R.string.chatting_setting_noti_on)
        } else {
            stringResource(R.string.chatting_setting_noti_off)
        }

        Row(
            modifier = settingItemModifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = notiText,
                color = colorResource(R.color.gray_870),
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            Switch(
                modifier = Modifier.size(48.dp, 24.dp),
                checked = onOff,
                onCheckedChange = { onOff = it })
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = settingItemModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.chatting_setting_leave),
                color = colorResource(R.color.gray_870),
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Preview
@Composable
fun ChattingSettingScreenPreview() {
    ChattingSettingScreen()
}