package com.aizid.playmov.ui.profile

import com.aizid.playmov.remote.dto.account.AccountDetails

data class ProfileUiState(
    val isLoading: Boolean = false,
    val accountDetails: AccountDetails? = null,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
)
