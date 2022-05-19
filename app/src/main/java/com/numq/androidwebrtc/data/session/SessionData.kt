package com.numq.androidwebrtc.data.session

import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.domain.repository.SessionRepository
import com.numq.androidwebrtc.platform.extension.withAvailableNetwork
import com.numq.androidwebrtc.service.network.NetworkHandler
import com.numq.androidwebrtc.service.rtc.RtcApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionData
@Inject constructor(
    private val networkHandler: NetworkHandler,
    private val service: RtcApi
) : SessionRepository {

    override val connectionState = networkHandler.withAvailableNetwork(service.connectionState)
    override val session = networkHandler.withAvailableNetwork(service.session)
    override val messages = networkHandler.withAvailableNetwork(service.messages)
    override fun requestSession() =
        networkHandler.withAvailableNetwork(service.requestSession())
    override fun leaveSession() =
        networkHandler.withAvailableNetwork(service.leaveSession())
    override fun sendMessage(message: Message) =
        networkHandler.withAvailableNetwork(service.sendMessage(message))

}
