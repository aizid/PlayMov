package com.aizid.playmov.ui.splash

import com.aizid.playmov.data.pref.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

class FakeTestPrefManager(private val onboardingDone: Boolean = false) : PrefManager(null) {
    override suspend fun isOnboardingCompleted(): Boolean = onboardingDone
}

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

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
    fun splashNavigation_transitionsAfterDelay() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()

        assertFalse(viewModel.uiState.value.navigateToGenre)

        testScheduler.advanceTimeBy(2100)

        assertTrue(viewModel.uiState.value.navigateToGenre)
        assertFalse(viewModel.uiState.value.isOnboardingCompleted)

        viewModel.resetNavigation()
        assertFalse(viewModel.uiState.value.navigateToGenre)
    }

    @Test
    fun splashNavigation_whenOnboardingCompleted_setsFlagTrue() = runTest(testDispatcher) {
        val fakePref = FakeTestPrefManager(onboardingDone = true)
        val viewModel = SplashViewModel(fakePref)

        testScheduler.advanceTimeBy(2100)

        assertTrue(viewModel.uiState.value.navigateToGenre)
        assertTrue(viewModel.uiState.value.isOnboardingCompleted)
    }
}
