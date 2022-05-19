package com.numq.androidwebrtc.platform.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.openSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}