package com.aizid.playmov.utils.device

data class DeviceInfo(
    val deviceId: String,
    val deviceName: String,
    val deviceOs: String,
    val ipAddress: String
)

expect fun getDeviceInfo(): DeviceInfo
