package com.numq.androidwebrtc.service.rtc

import android.util.Log
import com.numq.androidwebrtc.domain.entity.ConnectionState
import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.domain.entity.Session
import com.numq.androidwebrtc.service.peer.PeerApi
import com.numq.androidwebrtc.service.signaling.SignalingApi
import com.numq.androidwebrtc.service.socket.SocketApi
import com.numq.androidwebrtc.service.type.MessageType
import com.numq.androidwebrtc.service.type.SessionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.SessionDescription
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RtcService @Inject constructor(
    private val socket: SocketApi,
    private val signaling: SignalingApi,
    private val peer: PeerApi
) : RtcApi, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    init {
        launch {
            socket.messages.consumeEach {
                onMessageReceived(it)
            }
        }
    }

    override var connectionState: Channel<ConnectionState> = Channel()
    override var session: MutableStateFlow<Session?> = MutableStateFlow(null)
    override var messages: Channel<Message> = Channel()

    private val onMessageReceived: (String) -> Unit = { text ->

        val message = JSONObject(text)

        if (message.has("type") && message.has("body")) {

            val type = message.getString("type")
            val body = JSONObject(message.getString("body"))

            Log.d(javaClass.simpleName, "Got new message of type: $type")

            when (type) {
                SessionType.CONNECTING.name -> {
                    connectionState.trySend(ConnectionState.CONNECTING)
                }
                SessionType.CONNECTED.name -> {
                    val newSession = Session(
                        body.getString("sessionId"),
                        body.getString("clientId"),
                        body.getString("strangerId"),
                        body.getBoolean("isLastConnected")
                    )
                    joinSession(newSession)
                    session.update { newSession }
                    connectionState.trySend(ConnectionState.CONNECTED)
                }
                SessionType.DISCONNECTED.name -> {
                    leaveSession()
                    session.update { null }
                    connectionState.trySend(ConnectionState.DISCONNECTED)
                }
                MessageType.OFFER.name -> {
                    val id = body.getString("id")
                    val sdp = body.getString("sdp")
                    onOfferReceived(id, sdp)
                }
                MessageType.ANSWER.name -> {
                    val sdp = body.getString("sdp")
                    onAnswerReceived(sdp)
                }
                MessageType.CANDIDATE.name -> {
                    val candidate = IceCandidate(
                        body.getString("sdpMid"),
                        body.getString("sdpMLineIndex").toInt(),
                        body.getString("sdp")
                    )
                    peer.onIceCandidateReceived(candidate)
                }
            }
        }
    }

    override fun requestSession() {
        socket.connect()
        signaling.request()
    }

    override fun joinSession(session: Session) {
        peer.createPeer(onIceCandidate, onNegotiationNeeded)
        peer.createChannel(onMessage)
        if (session.isLastConnected) {
            sendOffer(session.strangerId, peer.constraints)
        }
    }

    override fun leaveSession() {
        signaling.leave()
        peer.close()
        socket.disconnect()
    }

    override fun sendMessage(message: Message) = peer.sendMessage(message)

    private val onMessage: (Message) -> Unit = { msg ->
        messages.trySend(msg)
    }

    private val onIceCandidate: (IceCandidate) -> Unit = { candidate ->
        session.value?.let {
            signaling.candidate(it.strangerId, candidate)
        }
    }

    private val onNegotiationNeeded: () -> Unit = {
        session.value?.let {
            if (it.isLastConnected) sendOffer(it.strangerId, peer.constraints)
        }
    }

    private fun onOfferReceived(id: String, sdp: String) {
        val offer = SessionDescription(SessionDescription.Type.OFFER, sdp)
        peer.onRemoteSdp(offer) {
            sendAnswer(id, peer.constraints)
        }
    }

    private fun onAnswerReceived(sdp: String) {
        val answer = SessionDescription(SessionDescription.Type.ANSWER, sdp)
        peer.onRemoteSdp(answer)
    }

    private fun sendOffer(id: String, constraints: MediaConstraints) {
        peer.sendOffer(constraints) {
            signaling.offer(id, it.description)
            Log.e(javaClass.simpleName, "Sent offer to id $id")
        }
    }

    private fun sendAnswer(id: String, constraints: MediaConstraints) {
        peer.sendAnswer(constraints) {
            signaling.answer(id, it.description)
            Log.e(javaClass.simpleName, "Sent answer to id $id")
        }
    }
}