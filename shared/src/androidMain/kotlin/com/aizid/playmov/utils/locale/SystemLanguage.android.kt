package com.aizid.playmov.utils.locale

import java.util.Locale

actual fun getSystemLanguage(): String {
    return if (Locale.getDefault().language.equals("id", ignoreCase = true)) "ID" else "EN"
}
