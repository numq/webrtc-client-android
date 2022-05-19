package com.numq.androidwebrtc.service.peer

import android.content.Context
import android.util.Log
import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.platform.extension.toJson
import com.numq.androidwebrtc.service.rtc.DefaultSdpObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import org.json.JSONObject
import org.webrtc.*
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class PeerService @Inject constructor(
    @ApplicationContext context: Context,
    private val eglContext: EglBase.Context
) : PeerApi, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    companion object {
        const val AUDIO = "0"
        const val VIDEO = "1"
        const val STREAM = "2"
        const val CHANNEL = "3"
    }

    override val constraints = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
    }

    private val iceUrls = listOf(
        "stun:stun.stunprotocol.org:3478",
        "stun:stun.l.google.com:19302"
    )

    private val iceServers = iceUrls.map(PeerConnection.IceServer::builder)
        .map(PeerConnection.IceServer.Builder::createIceServer)

    private fun initPeerConnectionFactory(context: Context) {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context).apply {
                setEnableInternalTracer(true)
                setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            }.createInitializationOptions()
        )
    }

    private val factory: PeerConnectionFactory = run {
        initPeerConnectionFactory(context)
        PeerConnectionFactory.builder().apply {
            setVideoEncoderFactory(DefaultVideoEncoderFactory(eglContext, true, true))
            setVideoDecoderFactory(DefaultVideoDecoderFactory(eglContext))
        }.createPeerConnectionFactory()
    }

    private var peer: PeerConnection? = null
    private var dataChannel: DataChannel? = null
    override var stream: Channel<MediaStream> = Channel()

    override fun createLocalAudioSource(constraints: MediaConstraints): AudioSource =
        factory.createAudioSource(constraints)

    override fun createLocalVideoSource(isScreencast: Boolean): VideoSource =
        factory.createVideoSource(isScreencast)

    override fun createLocalAudioTrack(source: AudioSource): AudioTrack =
        factory.createAudioTrack(AUDIO, source)

    override fun createLocalVideoTrack(source: VideoSource): VideoTrack =
        factory.createVideoTrack(VIDEO, source)

    override fun createLocalStream(audioTrack: AudioTrack, videoTrack: VideoTrack) =
        factory.createLocalMediaStream(STREAM)?.apply {
            addTrack(audioTrack)
            addTrack(videoTrack)
        }

    override fun createPeer(
        onIceCandidate: (IceCandidate) -> Unit,
        onNegotiationNeeded: () -> Unit
    ) {
        val peerObserver = object : DefaultPeerObserver() {

            override fun onIceCandidate(p0: IceCandidate) {
                super.onIceCandidate(p0)
                onIceCandidate(p0)
            }

            override fun onAddStream(p0: MediaStream) {
                super.onAddStream(p0)
                stream.trySend(p0)
            }

            override fun onRenegotiationNeeded() {
                super.onRenegotiationNeeded()
                onNegotiationNeeded()
            }

            override fun onDataChannel(p0: DataChannel?) {
                super.onDataChannel(p0)
                dataChannel = p0
            }
        }
        peer = factory.createPeerConnection(iceServers, peerObserver)
    }

    override fun createChannel(onChannelMessage: (Message) -> Unit) {
        val channelObserver = object : DefaultDataChannelObserver() {
            override fun onMessage(p0: DataChannel.Buffer) {
                super.onMessage(p0)
                if (p0.binary) {
                    Log.d(javaClass.simpleName, "Got binary message")
                    return
                }
                val json = JSONObject(charset(Charsets.UTF_8.name()).decode(p0.data).toString())
                if (json.has("senderId") && json.has("text")) {
                    onChannelMessage(
                        Message(
                            json.getString("senderId"),
                            json.getString("text")
                        )
                    )
                }
            }
        }
        dataChannel = peer?.createDataChannel(CHANNEL, DataChannel.Init())?.apply {
            registerObserver(channelObserver)
        }
    }

    override fun close() {
        dataChannel?.close()
        peer?.close()
        dataChannel = null
        peer = null
    }

    override fun sendMessage(message: Message) {
        val data = message.toJson().toByteArray()
        dataChannel?.send(DataChannel.Buffer(ByteBuffer.wrap(data), false))
    }

    override fun sendOffer(
        constraints: MediaConstraints,
        onSuccess: (SessionDescription) -> Unit
    ) {
        peer?.createOffer(object : DefaultSdpObserver() {
            override fun onCreateSuccess(sdp: SessionDescription) {
                super.onCreateSuccess(sdp)
                peer?.setLocalDescription(object : DefaultSdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        onSuccess(sdp)
                    }
                }, sdp)
            }
        }, constraints)
    }

    override fun sendAnswer(
        constraints: MediaConstraints,
        onSuccess: (SessionDescription) -> Unit
    ) {
        peer?.createAnswer(object : DefaultSdpObserver() {
            override fun onCreateSuccess(sdp: SessionDescription) {
                super.onCreateSuccess(sdp)
                peer?.setLocalDescription(object : DefaultSdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        onSuccess(sdp)
                    }
                }, sdp)
            }
        }, constraints)
    }

    override fun addStream(stream: MediaStream?) {
        peer?.addStream(stream)
    }

    override fun onRemoteSdp(sdp: SessionDescription, onSuccess: (SessionDescription) -> Unit) {
        peer?.setRemoteDescription(object : DefaultSdpObserver() {
            override fun onSetSuccess() {
                super.onSetSuccess()
                onSuccess(sdp)
            }
        }, sdp)
    }

    override fun onIceCandidateReceived(candidate: IceCandidate) {
        peer?.addIceCandidate(candidate)
    }
}