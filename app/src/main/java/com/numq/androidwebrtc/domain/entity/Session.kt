package com.numq.androidwebrtc.domain.entity

data class Session(
    val id: String,
    val clientId: String,
    val strangerId: String,
    val isLastConnected: Boolean
)