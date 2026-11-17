package com.aizid.playmov.utils.locale

import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import java.util.Locale

@Composable
actual fun ProvideAppLocale(language: String, content: @Composable () -> Unit) {
    val javaLocale = if (language == "EN") Locale.ENGLISH else Locale("id", "ID")
    Locale.setDefault(javaLocale)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        LocaleList.setDefault(LocaleList(javaLocale))
    }
    key(language) {
        content()
    }
}
