package com.numq.androidwebrtc.platform.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.numq.androidwebrtc.platform.constant.AppConstants
import com.numq.androidwebrtc.presentation.features.chat.Chat
import com.numq.androidwebrtc.presentation.features.home.Home

@Composable
fun AppRouter() {

    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()

    Scaffold(scaffoldState = scaffoldState) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            NavHost(navController = navController, startDestination = AppConstants.Nav.HOME) {
                composable(AppConstants.Nav.HOME) {
                    Home(scaffoldState, navController)
                }
                composable(AppConstants.Nav.CHAT) {
                    Chat(scaffoldState, navController)
                }
            }
        }
    }
}