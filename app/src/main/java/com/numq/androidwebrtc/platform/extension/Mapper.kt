package com.numq.androidwebrtc.platform.extension

import com.numq.androidwebrtc.domain.entity.Message
import org.json.JSONObject

fun Message.toJson() = JSONObject().apply {
    put("senderId", senderId)
    put("text", text)
    put("sentAt", sentAt)
}.toString()