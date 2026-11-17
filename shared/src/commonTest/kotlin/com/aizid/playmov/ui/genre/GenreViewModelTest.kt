package com.aizid.playmov.ui.genre

import com.aizid.playmov.remote.dto.Genre
import com.aizid.playmov.remote.dto.GenreResponse
import com.aizid.playmov.testutil.FakeMonoRepo
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.FailureData
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class GenreViewModelTest {

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
    fun loadGenres_populatesGenresOnSuccess() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            genreMovieListResult = Resource.Success(
                GenreResponse(listOf(Genre(28, "Action"), Genre(35, "Comedy"))),
                DataSource.REMOTE
            )
        }

        val viewModel = GenreViewModel(repo)
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(2, viewModel.uiState.value.movieGenres.size)
        assertEquals("Action", viewModel.uiState.value.movieGenres[0].name)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun loadGenres_setsErrorMessageOnFailure() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            genreMovieListResult = Resource.Failure(FailureData(500, "Server connection failed"))
        }

        val viewModel = GenreViewModel(repo)
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.movieGenres.isEmpty())
        assertEquals("Server connection failed", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onTabSelected_updatesSelectedTab() = runTest(testDispatcher) {
        val repo = FakeMonoRepo()
        val viewModel = GenreViewModel(repo)

        viewModel.onTabSelected(1)
        assertEquals(1, viewModel.uiState.value.selectedTab)
    }
}
