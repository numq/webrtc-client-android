package com.numq.androidwebrtc.domain.entity

data class Message(
    val senderId: String,
    val text: String,
    val sentAt: Long = System.currentTimeMillis()
)
