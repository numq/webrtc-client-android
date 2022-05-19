package com.numq.androidwebrtc.service.signaling

import com.numq.androidwebrtc.service.socket.SocketApi
import com.numq.androidwebrtc.service.type.MessageType
import org.json.JSONObject
import org.webrtc.IceCandidate
import javax.inject.Inject

class SignalingService @Inject constructor(private val socket: SocketApi) :
    SignalingApi {

    private fun sendSdp(id: String, type: String, sdp: String) =
        socket.sendMessage(type, JSONObject().apply {
            put("id", id)
            put("sdp", sdp)
        })

    override fun request() = socket.sendMessage(MessageType.REQUEST.name)

    override fun leave() = socket.sendMessage(MessageType.LEAVE.name)

    private fun sendCandidate(id: String, candidate: IceCandidate) =
        socket.sendMessage(MessageType.CANDIDATE.name, JSONObject().apply {
            put("id", id)
            put("sdp", candidate.sdp)
            put("sdpMLineIndex", candidate.sdpMLineIndex)
            put("sdpMid", candidate.sdpMid)
        })

    override fun offer(id: String, sdp: String) =
        sendSdp(id, MessageType.OFFER.name, sdp)

    override fun answer(id: String, sdp: String) =
        sendSdp(id, MessageType.ANSWER.name, sdp)

    override fun candidate(id: String, candidate: IceCandidate) =
        sendCandidate(id, candidate)
}