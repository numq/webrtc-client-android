package com.numq.androidwebrtc.service.peer

import android.util.Log
import org.webrtc.*

open class DefaultPeerObserver : PeerConnection.Observer {

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Log.i(javaClass.simpleName, "onSignalingChange: $p0")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Log.i(javaClass.simpleName, "onIceConnectionChange: $p0")
    }

    override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        super.onStandardizedIceConnectionChange(newState)
        Log.i(javaClass.simpleName, "onStandardizedIceConnectionChange: $newState")
    }

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
        super.onConnectionChange(newState)
        Log.i(javaClass.simpleName, "onConnectionChange: $newState")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Log.i(javaClass.simpleName, "onIceConnectionReceivingChange")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Log.i(javaClass.simpleName, "onIceGatheringChange: $p0")
    }

    override fun onIceCandidate(p0: IceCandidate) {
        Log.d(javaClass.simpleName, "onIceCandidate")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Log.d(javaClass.simpleName, "onIceCandidatesRemoved")
    }

    override fun onSelectedCandidatePairChanged(event: CandidatePairChangeEvent?) {
        Log.d(javaClass.simpleName, "onSelectedCandidatePairChanged")
    }

    override fun onAddStream(p0: MediaStream) {
        Log.d(javaClass.simpleName, "onAddStream")
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Log.d(javaClass.simpleName, "onRemoveStream")
    }

    override fun onDataChannel(p0: DataChannel?) {
        Log.d(javaClass.simpleName, "onDataChannel")
    }

    override fun onRenegotiationNeeded() {
        Log.d(javaClass.simpleName, "onRenegotiationNeeded")
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        Log.d(javaClass.simpleName, "onAddTrack")
    }

    override fun onTrack(transceiver: RtpTransceiver?) {
        Log.d(javaClass.simpleName, "onTrack")
    }
}