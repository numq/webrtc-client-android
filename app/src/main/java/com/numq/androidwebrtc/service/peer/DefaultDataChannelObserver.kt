package com.numq.androidwebrtc.service.peer

import android.util.Log
import org.webrtc.DataChannel

open class DefaultDataChannelObserver : DataChannel.Observer {
    override fun onBufferedAmountChange(p0: Long) {
        Log.d(javaClass.simpleName, "onBufferedAmountChange")
    }

    override fun onStateChange() {
        Log.d(javaClass.simpleName, "onStateChange")
    }

    override fun onMessage(p0: DataChannel.Buffer) {
        Log.d(javaClass.simpleName, "onMessage")
    }
}