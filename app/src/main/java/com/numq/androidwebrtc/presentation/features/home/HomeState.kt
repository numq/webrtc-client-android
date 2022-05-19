package com.numq.androidwebrtc.presentation.features.home

import com.numq.androidwebrtc.domain.entity.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class HomeState(
    val connectionState: ConnectionState = ConnectionState.IDLE,
    val exception: Exception? = null
)