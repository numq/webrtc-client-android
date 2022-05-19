package com.numq.androidwebrtc.service.signaling

import org.webrtc.IceCandidate

interface SignalingApi {
    fun request()
    fun leave()
    fun offer(id: String, sdp: String)
    fun answer(id: String, sdp: String)
    fun candidate(id: String, candidate: IceCandidate)
}