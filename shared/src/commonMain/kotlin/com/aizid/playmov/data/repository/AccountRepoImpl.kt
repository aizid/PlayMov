package com.aizid.playmov.data.repository

import com.aizid.playmov.data.pref.PrefManager
import com.aizid.playmov.domain.repository.AccountRepo
import com.aizid.playmov.remote.datasource.AccountDataSource
import com.aizid.playmov.remote.dto.account.*
import com.aizid.playmov.utils.device.getDeviceInfo
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.FailureData
import com.aizid.playmov.utils.network.NetworkCodes
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AccountRepoImpl(
    private val remoteSource: AccountDataSource,
    private val prefManager: PrefManager
) : AccountRepo {

    override suspend fun register(
        phoneNumber: String,
        name: String,
        password: String,
        confirmPassword: String,
        email: String?,
        nip: String?
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            val device = getDeviceInfo()
            val input = RegisterInput(
                phoneNumber = phoneNumber,
                email = email?.takeIf { it.isNotBlank() },
                nip = nip?.takeIf { it.isNotBlank() },
                password = password,
                confirmPassword = confirmPassword,
                name = name,
                deviceId = device.deviceId,
                deviceName = device.deviceName,
                deviceOs = device.deviceOs,
                ipAddress = device.ipAddress
            )
            val response = remoteSource.register(input)
            if (response.authToken.isNotBlank()) {
                prefManager.setAuthToken(response.authToken)
            }
            emit(Resource.Success(data = response, DataSource.REMOTE))
        } catch (e: Throwable) {
            emit(Resource.Failure(FailureData(code = NetworkCodes.GENERIC_ERROR, message = e.message)))
        }
    }

    override suspend fun validateRegisterAccount(
        code: String,
        phoneNumber: String,
        name: String,
        email: String?,
        nip: String?,
        registrationToken: String?
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            val input = ValidateRegisterInput(
                code = code,
                phoneNumber = phoneNumber,
                name = name,
                email = email?.takeIf { it.isNotBlank() },
                nip = nip?.takeIf { it.isNotBlank() }
            )
            val tokenToUse = registrationToken?.takeIf { it.isNotBlank() }
                ?: prefManager.getAuthToken().takeIf { it.isNotBlank() }
            val response = remoteSource.validateRegisterAccount(input, tokenToUse)
            if (response.authToken.isNotBlank()) {
                prefManager.setAuthToken(response.authToken)
            }
            emit(Resource.Success(data = response, DataSource.REMOTE))
        } catch (e: Throwable) {
            emit(Resource.Failure(FailureData(code = NetworkCodes.GENERIC_ERROR, message = e.message)))
        }
    }

    override suspend fun login(
        password: String,
        identifier: String
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            val device = getDeviceInfo()
            val input = LoginInput(
                identifier = identifier,
                password = password,
                deviceId = device.deviceId,
                deviceName = device.deviceName,
                deviceOs = device.deviceOs,
                ipAddress = device.ipAddress
            )
            val response = remoteSource.login(input)
            if (response.authToken.isNotBlank()) {
                prefManager.setAuthToken(response.authToken)
            }
            response.account?.accId?.let { accId ->
                prefManager.setAccountId(accId)
            }
            emit(Resource.Success(data = response, DataSource.REMOTE))
        } catch (e: Throwable) {
            emit(Resource.Failure(FailureData(code = NetworkCodes.GENERIC_ERROR, message = e.message)))
        }
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
        try {
            val token = prefManager.getAuthToken()
            val response = remoteSource.getAccountDetails(accId, token)
            prefManager.setAccountName(response.name)
            emit(Resource.Success(data = response, DataSource.REMOTE))
        } catch (e: Throwable) {
            emit(Resource.Failure(FailureData(code = NetworkCodes.GENERIC_ERROR, message = e.message)))
        }
    }

    override suspend fun isLoggedIn(): Boolean = prefManager.isLoggedIn()

    override suspend fun getAccountId(): String = prefManager.getAccountId()

    override suspend fun logout() = prefManager.clearAuth()
}
