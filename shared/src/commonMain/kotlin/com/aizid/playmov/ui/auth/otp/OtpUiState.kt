package com.aizid.playmov.ui.auth.otp

data class OtpUiState(
    val otpCode: String = "",
    val otpLength: Int = 6,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val resendCountdown: Int = 60,
    val canResend: Boolean = false
)
