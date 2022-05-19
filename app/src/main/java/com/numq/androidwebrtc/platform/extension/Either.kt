package com.numq.androidwebrtc.platform.extension

import arrow.core.Either

fun <T> T.rightOrException(condition: Boolean, exception: Exception) = when (condition) {
    true -> Either.Right(this)
    false -> Either.Left(exception)
}