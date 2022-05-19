package com.numq.androidwebrtc.service.rtc

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class DefaultSdpObserver : SdpObserver {
    override fun onCreateSuccess(sdp: SessionDescription) {
        Log.d(javaClass.simpleName, "onCreateSuccess")
    }

    override fun onSetSuccess() {
        Log.d(javaClass.simpleName, "onSetSuccess")
    }

    override fun onCreateFailure(p0: String?) {
        Log.e(javaClass.simpleName, "onCreateFailure: $p0")
    }

    override fun onSetFailure(p0: String?) {
        Log.e(javaClass.simpleName, "onSetFailure: $p0")
    }
}