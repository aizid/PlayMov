package com.aizid.playmov.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aizid.playmov.domain.repository.AccountRepo
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val accountRepo: AccountRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update {
            it.copy(
                name = newName,
                nameError = null,
                generalError = null
            )
        }
    }

    fun onPhoneNumberChange(newPhone: String) {
        _uiState.update {
            it.copy(
                phoneNumber = newPhone,
                phoneNumberError = null,
                generalError = null
            )
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null,
                generalError = null
            )
        }
    }

    fun onNipChange(newNip: String) {
        _uiState.update {
            it.copy(
                nip = newNip,
                generalError = null
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = null,
                confirmPasswordError = if (it.confirmPassword.isNotEmpty() && newPassword != it.confirmPassword) {
                    "Passwords do not match"
                } else null,
                generalError = null
            )
        }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = newConfirmPassword,
                confirmPasswordError = if (it.password != newConfirmPassword) {
                    "Passwords do not match"
                } else null,
                generalError = null
            )
        }
    }

    fun onRegisterClick() {
        val state = _uiState.value
        val name = state.name.trim()
        val phone = state.phoneNumber.trim()
        val email = state.email.trim()
        val nip = state.nip.trim()
        val password = state.password
        val confirmPassword = state.confirmPassword

        var hasError = false
        var nameErr: String? = null
        var phoneErr: String? = null
        var emailErr: String? = null
        var passwordErr: String? = null
        var confirmPasswordErr: String? = null

        if (name.isBlank()) {
            nameErr = "Full name is required"
            hasError = true
        } else if (name.length < 2) {
            nameErr = "Name must be at least 2 characters"
            hasError = true
        }

        if (phone.isBlank()) {
            phoneErr = "Phone number is required"
            hasError = true
        } else if (phone.length < 9) {
            phoneErr = "Enter a valid phone number (e.g. +62812...)"
            hasError = true
        }

        if (email.isNotBlank() && (!email.contains("@") || !email.contains("."))) {
            emailErr = "Please enter a valid email address"
            hasError = true
        }

        if (password.isBlank()) {
            passwordErr = "Password is required"
            hasError = true
        } else if (password.length < 6) {
            passwordErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordErr = "Please confirm your password"
            hasError = true
        } else if (password != confirmPassword) {
            confirmPasswordErr = "Passwords do not match"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    nameError = nameErr,
                    phoneNumberError = phoneErr,
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr
                )
            }
            return
        }

        viewModelScope.launch {
            accountRepo.register(
                phoneNumber = phone,
                name = name,
                password = password,
                confirmPassword = confirmPassword,
                email = email.ifBlank { null },
                nip = nip.ifBlank { null }
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, generalError = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                registeredEmail = email,
                                registeredPhone = phone,
                                registeredName = name,
                                registrationToken = result.data?.authToken
                            )
                        }
                    }
                    is Resource.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                generalError = result.failureData.message ?: "Registration failed. Please try again."
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    fun resetRegisteredEmail() {
        _uiState.update {
            it.copy(
                registeredEmail = null,
                registeredPhone = null,
                registeredName = null,
                registrationToken = null
            )
        }
    }
}
