package com.aizid.playmov.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aizid.playmov.data.pref.PrefManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class SplashUiState(
    val navigateToGenre: Boolean = false,
    val isOnboardingCompleted: Boolean = false
)

class SplashViewModel(
    private val prefManager: PrefManager = PrefManager()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isOnboardingCompleted = prefManager.isOnboardingCompleted()
            // Keep splash for a while
            delay(2000.milliseconds)

            _uiState.update {
                it.copy(
                    navigateToGenre = true,
                    isOnboardingCompleted = isOnboardingCompleted
                )
            }
        }
    }
    fun resetNavigation() {
        _uiState.update { it.copy(navigateToGenre = false) }
    }
}