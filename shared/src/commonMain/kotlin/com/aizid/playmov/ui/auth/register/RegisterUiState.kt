package com.aizid.playmov.ui.auth.register

data class RegisterUiState(
    val name: String = "",
    val nameError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val nip: String = "",
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val registeredEmail: String? = null,
    val registeredPhone: String? = null,
    val registeredName: String? = null,
    val registrationToken: String? = null
)
