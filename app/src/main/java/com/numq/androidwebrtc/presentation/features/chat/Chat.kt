package com.numq.androidwebrtc.presentation.features.chat

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.numq.androidwebrtc.domain.entity.ConnectionState
import com.numq.androidwebrtc.domain.entity.Message
import com.numq.androidwebrtc.domain.entity.Session
import com.numq.androidwebrtc.platform.constant.AppConstants
import com.numq.androidwebrtc.platform.extension.navigateSafe
import com.numq.androidwebrtc.presentation.features.failure.FailureSnackbar
import org.webrtc.SurfaceViewRenderer
import java.text.SimpleDateFormat

@Composable
fun Chat(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavController = rememberNavController(),
    vm: ChatViewModel = hiltViewModel()
) {
    val createView: @Composable (Modifier, ((SurfaceViewRenderer) -> Unit)) -> Unit =
        { modifier, callback ->
            AndroidView({ ctx ->
                SurfaceViewRenderer(ctx).apply(callback)
            }, modifier = modifier)
        }

    val state by vm.state.collectAsState()

    fun nextStranger() {
        vm.leaveSession()
        navController.navigateSafe(AppConstants.Nav.HOME)
    }

    LaunchedEffect(state.connectionState) {
        Log.d("STATE", state.connectionState.name)
    }

    when (state.connectionState) {
        ConnectionState.CONNECTED -> {
            state.session?.let { session ->
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.BottomStart) {
                        createView(Modifier.fillMaxSize(), vm.initRemote)
                        Box(Modifier.padding(8.dp)) {
                            createView(Modifier.fillMaxSize(.3f), vm.initLocal)
                        }
                    }
                    BuildControls(
                        nextStranger = {
                            vm.leaveSession()
                            navController.navigate(AppConstants.Nav.HOME)
                        },
                        toggleCameraView = vm.switchCameraView
                    )
                    BuildMessaging(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(.4f),
                        session,
                        state.messages
                    ) {
                        vm.sendMessage(it)
                    }
                }
            }
        }
        else -> nextStranger()
    }

    state.exception?.let {
        FailureSnackbar(scaffoldState, it)
    }
}

@Composable
fun BuildControls(
    nextStranger: () -> Unit,
    toggleCameraView: () -> Unit
) {
    Card {
        Row {
            IconButton(nextStranger) {
                Icon(Icons.Rounded.CallEnd, "", tint = Color.Red)
            }
            IconButton(toggleCameraView) {
                Icon(Icons.Rounded.Cameraswitch, "")
            }
        }
    }
}

@Composable
fun BuildMessaging(
    modifier: Modifier,
    session: Session,
    messages: List<Message>,
    sendMessage: (Message) -> Unit
) {
    var inputText by remember {
        mutableStateOf("")
    }
    Card {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn() {
                items(messages) { message ->
                    MessageItem(session, message)
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                TextField(value = inputText, onValueChange = {
                    inputText = it
                }, trailingIcon = {
                    IconButton(onClick = {
                        inputText = ""
                    }) {
                        Icon(Icons.Rounded.Clear, "")
                    }
                }, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    sendMessage(Message(session.clientId, inputText))
                    inputText = ""
                }, enabled = inputText.isNotBlank()) {
                    Icon(Icons.Rounded.Send, "")
                }
            }
        }
    }
}

@Composable
fun MessageItem(session: Session, message: Message) {
    val isOwner = session.clientId == message.senderId
    Box(
        contentAlignment = if (isOwner) Alignment.CenterEnd else Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                    Text(if (isOwner) "You" else "Stranger")
                }
                Divider()
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    Text(message.text)
                }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                    Text(SimpleDateFormat.getTimeInstance().format(message.sentAt))
                }
            }
        }
    }
}