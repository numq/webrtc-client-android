package com.numq.androidwebrtc.service.socket

import com.numq.androidwebrtc.platform.constant.AppConstants
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import javax.inject.Inject

class SocketService @Inject constructor() : SocketApi {

    companion object {
        const val DEFAULT_CODE = 1000
    }

    private val client = OkHttpClient.Builder().build()
    private val request = Request.Builder().apply {
        url(AppConstants.Socket.DEFAULT_URL)
    }.build()

    private var socket: WebSocket? = null
    override val messages = Channel<String>(Channel.CONFLATED)

    private val listener = object : DefaultSocketListener() {

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            messages.trySend(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            messages.trySend(bytes.utf8())
        }
    }

    private fun createSocket(request: Request, listener: WebSocketListener) =
        client.newWebSocket(request, listener)

    override fun sendMessage(type: String, body: JSONObject) {
        socket?.send(JSONObject().apply {
            put("type", type)
            put("body", body)
        }.toString())
    }

    override fun connect() {
        socket = createSocket(request, listener)
    }

    override fun disconnect() {
        socket?.close(DEFAULT_CODE, DEFAULT_CODE.toString())
        socket = null
    }
}