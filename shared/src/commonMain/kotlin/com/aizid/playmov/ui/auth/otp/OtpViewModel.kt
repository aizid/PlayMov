package com.aizid.playmov.ui.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aizid.playmov.domain.repository.AccountRepo
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OtpViewModel(
    private val accountRepo: AccountRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startResendTimer()
    }

    fun onOtpChange(newCode: String) {
        val filtered = newCode.filter { it.isDigit() }.take(_uiState.value.otpLength)
        _uiState.update {
            it.copy(
                otpCode = filtered,
                errorMessage = null
            )
        }
    }

    fun onVerifyClick(
        code: String,
        phoneNumber: String,
        name: String,
        email: String?,
        registrationToken: String? = null
    ) {
        val state = _uiState.value
        val otp = code.ifBlank { state.otpCode }
        if (otp.length < state.otpLength) {
            _uiState.update {
                it.copy(errorMessage = "Please enter all ${state.otpLength} digits of your verification code")
            }
            return
        }

        viewModelScope.launch {
            accountRepo.validateRegisterAccount(
                code = otp,
                phoneNumber = phoneNumber,
                name = name,
                email = email?.takeIf { it.isNotBlank() },
                registrationToken = registrationToken?.takeIf { it.isNotBlank() }
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true
                            )
                        }
                    }
                    is Resource.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.failureData.message ?: "Verification failed. Invalid or expired OTP."
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    fun onResendOtpClick(phoneNumber: String) {
        if (!_uiState.value.canResend) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    canResend = false,
                    resendCountdown = 60,
                    errorMessage = null
                )
            }
            startResendTimer()
        }
    }

    private fun startResendTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var timeLeft = 60
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
                _uiState.update { it.copy(resendCountdown = timeLeft) }
            }
            _uiState.update { it.copy(canResend = true) }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
