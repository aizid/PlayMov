package com.aizid.playmov.utils.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import platform.Foundation.NSUserDefaults

@Composable
actual fun ProvideAppLocale(language: String, content: @Composable () -> Unit) {
    // Persist preferred language for next app launch (iOS requires restart for full effect)
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(if (language == "EN") "en" else "id"),
        forKey = "AppleLanguages"
    )
    NSUserDefaults.standardUserDefaults.synchronize()
    key(language) {
        content()
    }
}
