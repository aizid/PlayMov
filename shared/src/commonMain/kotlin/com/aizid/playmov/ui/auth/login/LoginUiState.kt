package com.aizid.playmov.ui.auth.login

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val isSuccess: Boolean = false
)
