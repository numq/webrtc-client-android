package com.numq.androidwebrtc.presentation.features.chat

import com.numq.androidwebrtc.domain.entity.ConnectionState
import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.domain.entity.Session

data class ChatState(
    val connectionState: ConnectionState = ConnectionState.CONNECTED,
    val session: Session? = null,
    val messages: List<Message> = listOf(),
    val exception: Exception? = null
)