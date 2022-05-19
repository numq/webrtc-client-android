package com.numq.androidwebrtc.usecase

import com.numq.androidwebrtc.domain.entity.ConnectionState
import com.numq.androidwebrtc.domain.repository.SessionRepository
import com.numq.androidwebrtc.platform.usecase.UseCase
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class GetConnectionState
@Inject constructor(private val repository: SessionRepository) :
    UseCase<Unit, Channel<ConnectionState>>() {

    override fun execute(args: Unit) = repository.connectionState
}