package com.aizid.playmov.ui.auth.login

import com.aizid.playmov.remote.dto.account.AccountSummary
import com.aizid.playmov.remote.dto.account.AuthResponse
import com.aizid.playmov.testutil.FakeAccountRepo
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.FailureData
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onLoginClick_validatesEmptyFields() = runTest(testDispatcher) {
        val repo = FakeAccountRepo()
        val viewModel = LoginViewModel(repo)

        viewModel.onLoginClick()

        assertEquals("Email address cannot be empty", viewModel.uiState.value.emailError)
        assertEquals("Password cannot be empty", viewModel.uiState.value.passwordError)
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun onLoginClick_validatesShortPassword() = runTest(testDispatcher) {
        val repo = FakeAccountRepo()
        val viewModel = LoginViewModel(repo)

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("123")
        viewModel.onLoginClick()

        assertNull(viewModel.uiState.value.emailError)
        assertEquals("Password must be at least 6 characters", viewModel.uiState.value.passwordError)
    }

    @Test
    fun onLoginClick_success_updatesIsSuccess() = runTest(testDispatcher) {
        val repo = FakeAccountRepo().apply {
            loginResult = Resource.Success(
                AuthResponse("login_jwt_token", AccountSummary("acc_1", "Jane")),
                DataSource.REMOTE
            )
        }
        val viewModel = LoginViewModel(repo)

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onLoginClick()
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
        assertNull(viewModel.uiState.value.generalError)

        viewModel.resetSuccess()
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun onLoginClick_failure_updatesGeneralError() = runTest(testDispatcher) {
        val repo = FakeAccountRepo().apply {
            loginResult = Resource.Failure(FailureData(401, "Invalid email or password"))
        }
        val viewModel = LoginViewModel(repo)

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onLoginClick()
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)
        assertEquals("Invalid email or password", viewModel.uiState.value.generalError)
    }
}
