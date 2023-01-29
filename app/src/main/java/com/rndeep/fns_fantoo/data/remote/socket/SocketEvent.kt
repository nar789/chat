package com.rndeep.fns_fantoo.data.remote.socket

object ChatSocketEvent {
    const val WELCOME = "welcome"
    const val CREATE_CONVERSATION = "createConversation"
    const val LOAD_CONVERSATION = "loadConversation"
    const val MESSAGE = "message"
    const val READ_INFO = "readinfo"
    const val LOAD_MESSAGE = "loadmessage"
    const val UPLOAD_IMAGE = "uploadImage"
    const val OUT_CONVERSATION = "outconversation"
    const val LEAVE = "leave"
    const val JOIN = "join"
    const val LOAD_READ_INFO = "loadreadinfo"
}

/*
package com.rndeep.fns_fantoo.data.remote.socket

enum class ChatSocketEvent(val event: String) {
    WELCOME("welcome"),
    CREATE_CONVERSATION("createConversation"),
    LOAD_CONVERSATION("loadConversation"),
    MESSAGE("message"),
    READ_INFO("readinfo"),
    LOAD_MESSAGE("loadmessage"),
    UPLOAD_IMAGE("uploadImage")
}
 */