package com.numq.androidwebrtc.platform.navigation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.numq.androidwebrtc.platform.exception.AppExceptions
import com.numq.androidwebrtc.platform.extension.openSettings
import com.numq.androidwebrtc.presentation.features.failure.FailureSnackbar
import com.numq.androidwebrtc.presentation.theme.AndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NavActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        setContent {

            val scope = rememberCoroutineScope()
            val permissionsState = rememberMultiplePermissionsState(permissions)

            PermissionsRequired(
                multiplePermissionsState = permissionsState,
                permissionsNotGrantedContent = {
                    scope.launch {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                    applicationContext.openSettings()
                },
                permissionsNotAvailableContent = {
                    val scaffoldState = rememberScaffoldState()
                    Scaffold {
                        Box(modifier = Modifier.padding(it), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Error, "", modifier = Modifier.size(128.dp))
                            FailureSnackbar(
                                scaffoldState,
                                AppExceptions.PermissionException
                            ) {
                                applicationContext.openSettings()
                            }
                        }
                    }
                }) {
                AndroidTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        AppRouter()
                    }
                }
            }
        }
    }
}