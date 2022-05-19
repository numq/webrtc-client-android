package com.numq.androidwebrtc.usecase

import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.domain.repository.SessionRepository
import com.numq.androidwebrtc.platform.usecase.UseCase
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class GetMessage
@Inject constructor(private val repository: SessionRepository) :
    UseCase<Unit, Channel<Message>>() {

    override fun execute(args: Unit) = repository.messages
}