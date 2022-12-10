package com.rndeep.fns_fantoo.data.remote.model.chat

//todo[inwha] 임시 리스트
data class ChatListResult(
    val chatId: Long = 3,
    val roomName: String = "하이",
    val chat: String = "ㅋㅋㅋㅋㅋㅋㅋ",
    val time: Long = System.currentTimeMillis(),
    var count: Long = 3,
    val profileImg: String = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832"
)
