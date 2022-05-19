package com.numq.androidwebrtc.service.socket

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

interface SocketApi {
    val messages: Channel<String>
    fun sendMessage(type: String, body: JSONObject = JSONObject())
    fun connect()
    fun disconnect()
}