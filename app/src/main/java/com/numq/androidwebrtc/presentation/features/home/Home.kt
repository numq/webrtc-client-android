package com.numq.androidwebrtc.presentation.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.numq.androidwebrtc.domain.entity.ConnectionState
import com.numq.androidwebrtc.platform.constant.AppConstants
import com.numq.androidwebrtc.platform.extension.navigateSafe
import com.numq.androidwebrtc.presentation.features.failure.FailureSnackbar

@Composable
fun Home(scaffoldState: ScaffoldState, navController: NavController) {

    val vm = hiltViewModel<HomeViewModel>()

    var hasVideo by remember {
        mutableStateOf(true)
    }

    var connecting by remember {
        mutableStateOf(false)
    }

    val state by vm.state.collectAsState()

    LaunchedEffect(state.connectionState) {
        connecting = state.connectionState == ConnectionState.CONNECTING
    }

    when (state.connectionState) {
        ConnectionState.CONNECTED -> navController.navigateSafe(AppConstants.Nav.CHAT)
        else -> {
            BuildIdle(connecting, onRequestChat = {
                vm.requestSession()
                connecting = true
            }, onLeaveChat = {
                vm.leaveSession()
                connecting = false
            })
        }
    }
    state.exception?.let {
        FailureSnackbar(scaffoldState, it)
    }
}

@Composable
fun BuildIdle(
    connecting: Boolean,
    onRequestChat: () -> Unit,
    onLeaveChat: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoadingButton(connecting, onRequestChat, onLeaveChat)
    }
}

@Composable
fun LoadingButton(loading: Boolean, start: () -> Unit, cancel: () -> Unit) {
    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.clickable {
                cancel()
            })
        }
    } else {
        Button(
            modifier = Modifier.padding(8.dp), onClick = start
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Search, "")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Next stranger")
            }
        }
    }
}