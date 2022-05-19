package com.numq.androidwebrtc.domain.repository

import arrow.core.Either
import com.numq.androidwebrtc.domain.entity.ConnectionState
import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.domain.entity.Session
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {

    val connectionState: Either<Exception, Channel<ConnectionState>>
    val session: Either<Exception, StateFlow<Session?>>
    val messages: Either<Exception, Channel<Message>>
    fun requestSession(): Either<Exception, Unit>
    fun leaveSession(): Either<Exception, Unit>
    fun sendMessage(message: Message): Either<Exception, Unit>

}