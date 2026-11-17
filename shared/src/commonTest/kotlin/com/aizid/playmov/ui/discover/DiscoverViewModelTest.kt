package com.aizid.playmov.ui.discover

import com.aizid.playmov.remote.dto.Movie
import com.aizid.playmov.remote.dto.MovieResponse
import com.aizid.playmov.testutil.FakeMonoRepo
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.FailureData
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {

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
    fun loadDiscover_populatesMoviesAndGenreName() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            movieDiscoverResult = Resource.Success(
                MovieResponse(
                    page = 1,
                    results = listOf(Movie(id = 1, title = "Iron Man")),
                    totalPages = 5,
                    totalResults = 100
                ),
                DataSource.REMOTE
            )
        }

        val viewModel = DiscoverViewModel(repo)
        viewModel.loadDiscover(genreId = 28, genreName = "Action Movies")
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Action Movies", viewModel.uiState.value.genreName)
        assertEquals(1, viewModel.uiState.value.movies.size)
        assertEquals("Iron Man", viewModel.uiState.value.movies[0].title)
        assertTrue(viewModel.uiState.value.hasMorePages)
    }

    @Test
    fun loadDiscover_handlesErrorState() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            movieDiscoverResult = Resource.Failure(FailureData(404, "Genre not found"))
        }

        val viewModel = DiscoverViewModel(repo)
        viewModel.loadDiscover(genreId = 9999, genreName = "Unknown")
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Genre not found", viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.movies.isEmpty())
    }
}
