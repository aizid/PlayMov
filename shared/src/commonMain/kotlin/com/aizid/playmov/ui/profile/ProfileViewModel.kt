package com.aizid.playmov.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aizid.playmov.data.pref.PrefManager
import com.aizid.playmov.domain.repository.AccountRepo
import com.aizid.playmov.remote.dto.account.AccountDetails
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountRepo: AccountRepo,
    private val prefManager: PrefManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val accId = prefManager.getAccountId()
            val cachedName = prefManager.getAccountName()

            if (accId.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        accountDetails = AccountDetails(
                            accId = "N/A",
                            name = cachedName.ifBlank { "User" }
                        )
                    )
                }
                return@launch
            }

            accountRepo.getAccountDetails(accId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                accountDetails = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                accountDetails = it.accountDetails ?: if (cachedName.isNotBlank()) {
                                    AccountDetails(accId = accId, name = cachedName)
                                } else null,
                                errorMessage = result.failureData.message ?: "Failed to load profile details"
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            accountRepo.logout()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}
