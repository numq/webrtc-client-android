package com.numq.androidwebrtc.usecase

import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.domain.repository.SessionRepository
import com.numq.androidwebrtc.platform.usecase.UseCase
import javax.inject.Inject

class SendMessage
@Inject constructor(private val repository: SessionRepository) : UseCase<Message, Unit>() {

    override fun execute(args: Message) = repository.sendMessage(args)
}