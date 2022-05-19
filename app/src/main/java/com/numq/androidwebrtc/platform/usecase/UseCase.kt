package com.numq.androidwebrtc.platform.usecase

import android.util.Log
import arrow.core.Either
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class UseCase<in T, out R> : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    abstract fun execute(arg: T): Either<Exception, R>

    operator fun invoke(arg: T, onResult: (Either<Exception, R>) -> Unit = {}) =
        launch {
            val result = async {
                execute(arg)
            }
            withContext(Dispatchers.Main) {
                onResult(result.await().tap {
                    Log.d(javaClass.simpleName, it.toString())
                })
            }
        }
}