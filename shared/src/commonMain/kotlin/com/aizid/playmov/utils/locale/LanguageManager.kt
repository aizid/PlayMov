package com.aizid.playmov.utils.locale

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageManager {
    private val _currentLanguage = MutableStateFlow("EN")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(language: String) {
        _currentLanguage.value = language
    }
}