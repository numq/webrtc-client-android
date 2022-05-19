package com.numq.androidwebrtc.service.peer

import com.numq.androidwebrtc.domain.entity.Message
import kotlinx.coroutines.channels.Channel
import org.webrtc.*

interface PeerApi {
    val constraints: MediaConstraints
    var stream: Channel<MediaStream>
    fun createLocalAudioSource(constraints: MediaConstraints): AudioSource
    fun createLocalVideoSource(isScreencast: Boolean): VideoSource
    fun createLocalAudioTrack(source: AudioSource): AudioTrack
    fun createLocalVideoTrack(source: VideoSource): VideoTrack
    fun createLocalStream(audioTrack: AudioTrack, videoTrack: VideoTrack): MediaStream?
    fun createPeer(
        onIceCandidate: (IceCandidate) -> Unit,
        onNegotiationNeeded: () -> Unit
    )

    fun createChannel(
        onChannelMessage: (Message) -> Unit
    )

    fun close()
    fun sendMessage(message: Message)
    fun sendOffer(
        constraints: MediaConstraints,
        onSuccess: (SessionDescription) -> Unit
    )

    fun sendAnswer(
        constraints: MediaConstraints,
        onSuccess: (SessionDescription) -> Unit
    )

    fun addStream(stream: MediaStream?)
    fun onRemoteSdp(sdp: SessionDescription, onSuccess: (SessionDescription) -> Unit = {})
    fun onIceCandidateReceived(candidate: IceCandidate)
}