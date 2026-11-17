package com.aizid.playmov.utils.device

import android.os.Build

actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    deviceId = "android-" + (Build.ID ?: "unknown"),
    deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
    deviceOs = "Android ${Build.VERSION.RELEASE}",
    ipAddress = "127.0.0.1"
)
