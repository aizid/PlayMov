package com.aizid.playmov.utils.device

import platform.UIKit.UIDevice

actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    deviceId = UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "ios-device",
    deviceName = UIDevice.currentDevice.name,
    deviceOs = "iOS " + UIDevice.currentDevice.systemVersion,
    ipAddress = "127.0.0.1"
)
