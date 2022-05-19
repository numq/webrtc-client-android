package com.numq.androidwebrtc.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numq.androidwebrtc.usecase.GetConnectionState
import com.numq.androidwebrtc.usecase.LeaveSession
import com.numq.androidwebrtc.usecase.RequestSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getConnectionState: GetConnectionState,
    private val requestSession: RequestSession,
    private val leaveSession: LeaveSession
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        getConnectionState.invoke(Unit) { data ->
            data.fold({ exception ->
                _state.update {
                    it.copy(exception = exception)
                }
            }, { channel ->
                viewModelScope.launch {
                    channel.consumeAsFlow().collect { connectionState ->
                        _state.update {
                            it.copy(connectionState = connectionState)
                        }
                    }
                }
            })
        }
    }

    fun requestSession() = requestSession.invoke(Unit)

    fun leaveSession() = leaveSession.invoke(Unit)
}