package com.aizid.playmov.ui.auth.register

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
class RegisterViewModelTest {

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
    fun onRegisterClick_validatesEmptyFields() = runTest(testDispatcher) {
        val repo = FakeAccountRepo()
        val viewModel = RegisterViewModel(repo)

        viewModel.onRegisterClick()

        assertEquals("Full name is required", viewModel.uiState.value.nameError)
        assertEquals("Phone number is required", viewModel.uiState.value.phoneNumberError)
        assertEquals("Password is required", viewModel.uiState.value.passwordError)
        assertEquals("Please confirm your password", viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun onRegisterClick_validatesPasswordMismatch() = runTest(testDispatcher) {
        val repo = FakeAccountRepo()
        val viewModel = RegisterViewModel(repo)

        viewModel.onNameChange("Jane Doe")
        viewModel.onPhoneNumberChange("+6281234567890")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("different456")
        viewModel.onRegisterClick()

        assertEquals("Passwords do not match", viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun onRegisterClick_success_storesRegistrationToken() = runTest(testDispatcher) {
        val repo = FakeAccountRepo().apply {
            registerResult = Resource.Success(
                AuthResponse("reg_jwt_session_token", AccountSummary("acc_1", "Jane")),
                DataSource.REMOTE
            )
        }
        val viewModel = RegisterViewModel(repo)

        viewModel.onNameChange("Jane Doe")
        viewModel.onPhoneNumberChange("+6281234567890")
        viewModel.onEmailChange("jane@test.com")
        viewModel.onPasswordChange("secret123")
        viewModel.onConfirmPasswordChange("secret123")
        viewModel.onRegisterClick()
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("jane@test.com", viewModel.uiState.value.registeredEmail)
        assertEquals("+6281234567890", viewModel.uiState.value.registeredPhone)
        assertEquals("Jane Doe", viewModel.uiState.value.registeredName)
        assertEquals("reg_jwt_session_token", viewModel.uiState.value.registrationToken)

        viewModel.resetRegisteredEmail()
        assertNull(viewModel.uiState.value.registrationToken)
    }

    @Test
    fun onRegisterClick_failure_setsGeneralError() = runTest(testDispatcher) {
        val repo = FakeAccountRepo().apply {
            registerResult = Resource.Failure(FailureData(400, "Phone number already registered"))
        }
        val viewModel = RegisterViewModel(repo)

        viewModel.onNameChange("Jane Doe")
        viewModel.onPhoneNumberChange("+6281234567890")
        viewModel.onPasswordChange("secret123")
        viewModel.onConfirmPasswordChange("secret123")
        viewModel.onRegisterClick()
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Phone number already registered", viewModel.uiState.value.generalError)
    }
}
