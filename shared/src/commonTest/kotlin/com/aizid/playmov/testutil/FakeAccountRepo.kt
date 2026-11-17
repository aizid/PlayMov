package com.aizid.playmov.testutil

import com.aizid.playmov.domain.repository.AccountRepo
import com.aizid.playmov.remote.dto.account.AccountDetails
import com.aizid.playmov.remote.dto.account.AccountSummary
import com.aizid.playmov.remote.dto.account.AuthResponse
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAccountRepo : AccountRepo {
    var registerResult: Resource<AuthResponse> = Resource.Success(
        AuthResponse(authToken = "reg_token_abc", account = AccountSummary("acc_1", "Jane")),
        DataSource.REMOTE
    )
    var validateResult: Resource<AuthResponse> = Resource.Success(
        AuthResponse(authToken = "verified_jwt"),
        DataSource.REMOTE
    )
    var loginResult: Resource<AuthResponse> = Resource.Success(
        AuthResponse(authToken = "login_jwt_token", account = AccountSummary("acc_1")),
        DataSource.REMOTE
    )
    var detailsResult: Resource<AccountDetails> = Resource.Success(
        AccountDetails(accId = "acc_1", name = "Jane Doe"),
        DataSource.REMOTE
    )

    var lastCodeValidated: String? = null
    var lastRegistrationTokenValidated: String? = null
    var isLoggedInValue: Boolean = false
    var accountIdValue: String = "acc_1"

    override suspend fun register(
        phoneNumber: String,
        name: String,
        password: String,
        confirmPassword: String,
        email: String?,
        nip: String?
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        emit(registerResult)
    }

    override suspend fun validateRegisterAccount(
        code: String,
        phoneNumber: String,
        name: String,
        email: String?,
        nip: String?,
        registrationToken: String?
    ): Flow<Resource<AuthResponse>> = flow {
        lastCodeValidated = code
        lastRegistrationTokenValidated = registrationToken
        emit(Resource.Loading)
        emit(validateResult)
    }

    override suspend fun login(
        password: String,
        identifier: String
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        emit(loginResult)
    }

    override suspend fun login(
        password: String,
        email: String?,
        nip: String?
    ): Flow<Resource<AuthResponse>> = login(
        password = password,
        identifier = email?.takeIf { it.isNotBlank() } ?: nip.orEmpty()
    )

    override suspend fun getAccountDetails(accId: String): Flow<Resource<AccountDetails>> = flow {
        emit(Resource.Loading)
        emit(detailsResult)
    }

    override suspend fun isLoggedIn(): Boolean = isLoggedInValue

    override suspend fun getAccountId(): String = accountIdValue

    override suspend fun logout() {
        isLoggedInValue = false
    }
}
