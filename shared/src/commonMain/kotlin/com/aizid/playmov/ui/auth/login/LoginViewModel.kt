package com.aizid.playmov.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aizid.playmov.domain.repository.AccountRepo
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val accountRepo: AccountRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null,
                generalError = null
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = null,
                generalError = null
            )
        }
    }

    fun onLoginClick() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password

        var hasError = false
        var emailErr: String? = null
        var passwordErr: String? = null

        if (email.isBlank()) {
            emailErr = "Email address cannot be empty"
            hasError = true
        }

        if (password.isBlank()) {
            passwordErr = "Password cannot be empty"
            hasError = true
        } else if (password.length < 6) {
            passwordErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr
                )
            }
            return
        }

        viewModelScope.launch {
            accountRepo.login(
                password = password,
                identifier = email
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, generalError = null) }
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
                                generalError = result.failureData.message ?: "Authentication failed"
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
