package com.aizid.playmov.utils.locale

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

actual fun getSystemLanguage(): String {
    val preferred = NSLocale.preferredLanguages.firstOrNull() as? String ?: return "EN"
    return if (preferred.startsWith("id", ignoreCase = true)) "ID" else "EN"
}
