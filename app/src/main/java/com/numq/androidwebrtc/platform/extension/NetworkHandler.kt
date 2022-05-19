package com.numq.androidwebrtc.platform.extension

import arrow.core.Either
import com.numq.androidwebrtc.platform.exception.AppExceptions
import com.numq.androidwebrtc.service.network.NetworkHandler

fun <T> NetworkHandler.withAvailableNetwork(value: T) = when (isNetworkAvailable) {
    true -> Either.Right(value)
    false -> Either.Left(AppExceptions.NetworkException)
}