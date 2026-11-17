package com.aizid.playmov.domain.repository

import com.aizid.playmov.remote.dto.account.AccountDetails
import com.aizid.playmov.remote.dto.account.AuthResponse
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.Flow

interface AccountRepo {
    suspend fun register(
        phoneNumber: String,
        name: String,
        password: String,
        confirmPassword: String,
        email: String? = null,
        nip: String? = null
    ): Flow<Resource<AuthResponse>>

    suspend fun validateRegisterAccount(
        code: String,
        phoneNumber: String,
        name: String,
        email: String? = null,
        nip: String? = null,
        registrationToken: String? = null
    ): Flow<Resource<AuthResponse>>

    suspend fun login(
        password: String,
        identifier: String
    ): Flow<Resource<AuthResponse>>

    suspend fun login(
        password: String,
        email: String? = null,
        nip: String? = null
    ): Flow<Resource<AuthResponse>> = login(
        password = password,
        identifier = email?.takeIf { it.isNotBlank() } ?: nip.orEmpty()
    )

    suspend fun getAccountDetails(accId: String): Flow<Resource<AccountDetails>>
    suspend fun isLoggedIn(): Boolean
    suspend fun getAccountId(): String
    suspend fun logout()
}
