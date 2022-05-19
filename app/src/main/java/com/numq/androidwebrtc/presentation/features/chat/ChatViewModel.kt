package com.numq.androidwebrtc.presentation.features.chat

import android.util.Log
import androidx.lifecycle.*
import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.service.rtc.RtcMediaService
import com.numq.androidwebrtc.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    getConnectionState: GetConnectionState,
    getSession: GetSession,
    getMessage: GetMessage,
    private val sendMessage: SendMessage,
    private val leaveSession: LeaveSession,
    private val rtcMediaService: RtcMediaService
) : ViewModel(), LifecycleEventObserver {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    init {
        getConnectionState.invoke(Unit) { data ->
            data.fold({ exception ->
                _state.update {
                    it.copy(exception = exception)
                }
            }, { channel ->
                viewModelScope.launch {
                    channel.receiveAsFlow().collect { connectionState ->
                        _state.update {
                            it.copy(connectionState = connectionState)
                        }
                    }
                }
            })
        }
        getSession.invoke(Unit) { data ->
            data.fold({ exception ->
                _state.update {
                    it.copy(exception = exception)
                }
            }, { flow ->
                viewModelScope.launch {
                    flow.collect { session ->
                        _state.update {
                            it.copy(session = session)
                        }
                    }
                }
            })
        }
        getMessage.invoke(Unit) { data ->
            data.fold({ exception ->
                _state.update {
                    it.copy(exception = exception)
                }
            }, { channel ->
                viewModelScope.launch {
                    channel.receiveAsFlow().collect { message ->
                        Log.d("MESSAGE", message.toString())
                        _state.update {
                            it.copy(messages = it.messages.plus(message))
                        }
                    }
                }
            })
        }
    }

    val initLocal = rtcMediaService::initLocalView
    val initRemote = rtcMediaService::initRemoteView
    val switchCameraView = rtcMediaService::switchCamera

    fun leaveSession() {
        leaveSession.invoke(Unit)
        rtcMediaService.dispose()
    }

    fun sendMessage(message: Message) {
        _state.update { it.copy(messages = it.messages.plus(message)) }
        sendMessage.invoke(message)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_PAUSE) {
            leaveSession()
        }
    }
}