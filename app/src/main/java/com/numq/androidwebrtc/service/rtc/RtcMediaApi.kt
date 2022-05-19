package com.numq.androidwebrtc.service.rtc

import org.webrtc.SurfaceViewRenderer

interface RtcMediaApi {
    fun initLocalView(view: SurfaceViewRenderer)
    fun initRemoteView(view: SurfaceViewRenderer)
    fun dispose()
    fun toggleAudio(state: Boolean)
    fun toggleVideo(state: Boolean)
    fun switchCamera()
}