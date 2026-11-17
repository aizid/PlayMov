package com.aizid.playmov.ui.auth.otp

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
class OtpViewModelTest {

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
    fun onOtpChange_filtersDigitsAndBoundsToSix() = runTest(testDispatcher) {
        val repo = FakeAccountRepo()
        val viewModel = OtpViewModel(repo)

        viewModel.onOtpChange("12a3b456789")
        assertEquals("123456", viewModel.uiState.value.otpCode)
    }

    @Test
    fun onVerifyClick_validatesOtpLength() = runTest(testDispatcher) {
        val repo = FakeAccountRepo()
        val viewModel = OtpViewModel(repo)

        viewModel.onOtpChange("123")
        viewModel.onVerifyClick("123", "+628123456", "Jane", "jane@test.com")

        assertEquals("Please enter all 6 digits of your verification code", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun onVerifyClick_success_signalsSuccess() = runTest(testDispatcher) {
        val repo = FakeAccountRepo().apply {
            validateResult = Resource.Success(
                AuthResponse("verified_token"),
                DataSource.REMOTE
            )
        }
        val viewModel = OtpViewModel(repo)

        viewModel.onVerifyClick("123456", "+628123456", "Jane", "jane@test.com", "reg_token_123")
        testScheduler.advanceUntilIdle()

        assertEquals("123456", repo.lastCodeValidated)
        assertEquals("reg_token_123", repo.lastRegistrationTokenValidated)
        assertTrue(viewModel.uiState.value.isSuccess)

        viewModel.resetSuccess()
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun onVerifyClick_failure_setsErrorMessage() = runTest(testDispatcher) {
        val repo = FakeAccountRepo().apply {
            validateResult = Resource.Failure(FailureData(400, "Invalid or expired OTP"))
        }
        val viewModel = OtpViewModel(repo)

        viewModel.onVerifyClick("000000", "+628123456", "Jane", "jane@test.com")
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertEquals("Invalid or expired OTP", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun resendCountdown_decrementsAndEnablesResend() = runTest(testDispatcher) {
        val repo = FakeAccountRepo()
        val viewModel = OtpViewModel(repo)

        assertFalse(viewModel.uiState.value.canResend)

        testScheduler.advanceTimeBy(61000)

        assertTrue(viewModel.uiState.value.canResend)
    }
}
