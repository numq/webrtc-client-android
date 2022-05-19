package com.numq.androidwebrtc.service.rtc

import com.numq.androidwebrtc.domain.entity.ConnectionState
import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.domain.entity.Session
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

interface RtcApi {
    var connectionState: Channel<ConnectionState>
    var session: MutableStateFlow<Session?>
    var messages: Channel<Message>
    fun requestSession()
    fun joinSession(session: Session)
    fun leaveSession()
    fun sendMessage(message: Message)
}