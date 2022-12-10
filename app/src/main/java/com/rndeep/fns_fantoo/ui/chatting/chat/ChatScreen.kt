package com.rndeep.fns_fantoo.ui.chatting.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rndeep.fns_fantoo.R

@Composable
fun ChatScreen(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Text("test")
    }
}

@Composable
fun Message() {

}

@Composable
fun AuthAndName(
    message: Message
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier
                .padding(start = 20.dp)
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp)),
            painter = painterResource(R.drawable.baby_icon),
            contentScale = ContentScale.Crop,
            contentDescription = ""
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = message.authorName,
            fontSize = 12.sp,
            color = Color(R.color.gray_400),
        )
    }
}

@Composable
fun MessageBubble() {

}

@Preview
@Composable
fun AuthAndNamePreview() {
    val message = Message(
        "당연히 같이 가야지~ 스탠딩 공연이잖아 너무 재밌을것 같어~",
        "Dasol"
    )
    MaterialTheme {
        AuthAndName(message = message)
    }
}