package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rndeep.fns_fantoo.R

@Composable
fun ChattingSettingScreen(
    isAlarmOn: Boolean,
    onClickAlarm: (Boolean) -> Unit,
    onClickLeave: () -> Unit
) {
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
            ChattingSettingContent(
                modifier = Modifier.padding(top = 10.dp),
                isAlarmOn = isAlarmOn,
                onClickAlarm = onClickAlarm,
                onClickLeave = onClickLeave
            )
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
    modifier: Modifier,
    isAlarmOn: Boolean,
    onClickAlarm: (Boolean) -> Unit,
    onClickLeave: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val settingItemModifier = Modifier
            .fillMaxWidth()
            .heightIn(34.dp)

        val notiText =
            stringResource(if (isAlarmOn) R.string.chatting_setting_noti_on else R.string.chatting_setting_noti_off)

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
            SettingSwitch(
                checked = isAlarmOn,
                onCheckedChange = { onClickAlarm(it) }
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = settingItemModifier.clickable { onClickLeave() },
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

@Composable
fun SettingSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    val width: Dp = 48.dp
    val height: Dp = 24.dp
    val checkedTrackColor: Color = colorResource(R.color.primary_300)
    val uncheckedTrackColor: Color = colorResource(R.color.state_disabled_gray_200)
    val thumbColor: Color = colorResource(R.color.gray_25)
    val gapBetweenThumbAndTrackEdge: Dp = 4.dp

    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge

    // To move the thumb, we need to calculate the position (along x axis)
    val animatePosition = animateFloatAsState(
        targetValue = if (checked)
            with(LocalDensity.current) { (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx() }
        else
            with(LocalDensity.current) { (thumbRadius + gapBetweenThumbAndTrackEdge).toPx() }
    )

    Canvas(
        modifier = Modifier
            .size(width = width, height = height)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        // Track
        drawRoundRect(
            color = if (checked) checkedTrackColor else uncheckedTrackColor,
            cornerRadius = CornerRadius(x = 56.dp.toPx(), y = 56.dp.toPx())
        )

        // Thumb
        drawCircle(
            color = thumbColor,
            radius = thumbRadius.toPx(),
            center = Offset(
                x = animatePosition.value,
                y = size.height / 2
            )
        )
    }
}

@Preview
@Composable
fun ChattingSettingScreenPreview() {
    ChattingSettingScreen(true, {}, {})
}