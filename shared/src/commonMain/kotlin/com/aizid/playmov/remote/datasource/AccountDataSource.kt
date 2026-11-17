package com.aizid.playmov.remote.datasource

import com.aizid.playmov.remote.dto.account.*

interface AccountDataSource {
    suspend fun register(input: RegisterInput): AuthResponse
    suspend fun validateRegisterAccount(input: ValidateRegisterInput, authToken: String? = null): AuthResponse
    suspend fun login(input: LoginInput): AuthResponse
    suspend fun getAccountDetails(accId: String, authToken: String? = null): AccountDetails
}
