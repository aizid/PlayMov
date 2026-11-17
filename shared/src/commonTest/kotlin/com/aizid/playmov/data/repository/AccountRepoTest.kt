package com.aizid.playmov.data.repository

import com.aizid.playmov.data.pref.PrefManager
import com.aizid.playmov.remote.datasource.AccountDataSource
import com.aizid.playmov.remote.dto.account.*
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeAccountDataSource : AccountDataSource {
    var registerResult: Result<AuthResponse> = Result.success(AuthResponse("reg_token_123", AccountSummary("acc_1", "Jane")))
    var validateResult: Result<AuthResponse> = Result.success(AuthResponse("final_token_999"))
    var loginResult: Result<AuthResponse> = Result.success(AuthResponse("login_token_abc", AccountSummary("acc_1")))
    var detailsResult: Result<AccountDetails> = Result.success(AccountDetails("acc_1", "Jane Doe", "jane@test.com"))

    var lastRegistrationTokenPassed: String? = null

    override suspend fun register(input: RegisterInput): AuthResponse {
        return registerResult.getOrThrow()
    }

    override suspend fun validateRegisterAccount(input: ValidateRegisterInput, authToken: String?): AuthResponse {
        lastRegistrationTokenPassed = authToken
        return validateResult.getOrThrow()
    }

    override suspend fun login(input: LoginInput): AuthResponse {
        return loginResult.getOrThrow()
    }

    override suspend fun getAccountDetails(accId: String, authToken: String?): AccountDetails {
        return detailsResult.getOrThrow()
    }
}

class FakePrefManager : PrefManager(null) {
    private val map = mutableMapOf<String, String>()

    override suspend fun getAuthToken(): String = map["token"] ?: ""
    override suspend fun setAuthToken(value: String) { map["token"] = value }

    override suspend fun getAccountId(): String = map["accId"] ?: ""
    override suspend fun setAccountId(value: String) { map["accId"] = value }

    override suspend fun getAccountName(): String = map["name"] ?: ""
    override suspend fun setAccountName(value: String) { map["name"] = value }
}

class AccountRepoTest {

    @Test
    fun register_emitsLoadingThenSuccess_andSavesToken() = runTest {
        val fakeSource = FakeAccountDataSource()
        val fakePref = FakePrefManager()
        val repo = AccountRepoImpl(fakeSource, fakePref)

        val emissions = repo.register(
            phoneNumber = "+62812345678",
            name = "Jane",
            password = "pass",
            confirmPassword = "pass"
        ).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        val data = (emissions[1] as Resource.Success).data
        assertEquals("reg_token_123", data?.authToken)
        assertEquals("reg_token_123", fakePref.getAuthToken())
    }

    @Test
    fun validateRegisterAccount_forwardsRegistrationToken_andSavesFinalToken() = runTest {
        val fakeSource = FakeAccountDataSource()
        val fakePref = FakePrefManager()
        val repo = AccountRepoImpl(fakeSource, fakePref)

        val emissions = repo.validateRegisterAccount(
            code = "123456",
            phoneNumber = "+62812345678",
            name = "Jane",
            registrationToken = "token_from_prev_step"
        ).toList()

        assertEquals("token_from_prev_step", fakeSource.lastRegistrationTokenPassed)
        assertTrue(emissions.last() is Resource.Success)
        assertEquals("final_token_999", fakePref.getAuthToken())
    }

    @Test
    fun login_emitsSuccess_andSavesTokenAndAccountId() = runTest {
        val fakeSource = FakeAccountDataSource()
        val fakePref = FakePrefManager()
        val repo = AccountRepoImpl(fakeSource, fakePref)

        val emissions = repo.login(
            password = "password123",
            identifier = "muhammad.faiz532@gmail.com"
        ).toList()

        assertTrue(emissions.last() is Resource.Success)
        assertEquals("login_token_abc", fakePref.getAuthToken())
        assertEquals("acc_1", fakePref.getAccountId())
    }

    @Test
    fun getAccountDetails_updatesAccountNameInPrefManager() = runTest {
        val fakeSource = FakeAccountDataSource()
        val fakePref = FakePrefManager()
        val repo = AccountRepoImpl(fakeSource, fakePref)

        val emissions = repo.getAccountDetails("acc_1").toList()

        assertTrue(emissions.last() is Resource.Success)
        val details = (emissions.last() as Resource.Success).data
        assertEquals("Jane Doe", details?.name)
        assertEquals("Jane Doe", fakePref.getAccountName())
    }

    @Test
    fun register_emitsFailure_whenRemoteDataSourceThrows() = runTest {
        val fakeSource = FakeAccountDataSource().apply {
            registerResult = Result.failure(RuntimeException("Phone number already taken"))
        }
        val fakePref = FakePrefManager()
        val repo = AccountRepoImpl(fakeSource, fakePref)

        val emissions = repo.register(
            phoneNumber = "+62812345678",
            name = "Jane",
            password = "pass",
            confirmPassword = "pass"
        ).toList()

        assertTrue(emissions.last() is Resource.Failure)
        val failure = (emissions.last() as Resource.Failure).failureData
        assertEquals("Phone number already taken", failure.message)
    }
}
