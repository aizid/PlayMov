package com.aizid.playmov.ui.search

import com.aizid.playmov.remote.dto.Movie
import com.aizid.playmov.remote.dto.MovieResponse
import com.aizid.playmov.testutil.FakeMonoRepo
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

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
    fun onQueryChange_triggersSearch_whenAtLeastTwoCharacters() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            searchMoviesResult = Resource.Success(
                MovieResponse(
                    page = 1,
                    results = listOf(Movie(id = 10, title = "Avatar")),
                    totalPages = 1,
                    totalResults = 1
                ),
                DataSource.REMOTE
            )
        }

        val viewModel = SearchViewModel(repo)
        viewModel.onQueryChange("Av")
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Av", viewModel.uiState.value.query)
        assertEquals(1, viewModel.uiState.value.searchResults.size)
        assertEquals("Avatar", viewModel.uiState.value.searchResults[0].title)
    }

    @Test
    fun onQueryChange_clearsResults_whenShorterThanTwoCharacters() = runTest(testDispatcher) {
        val repo = FakeMonoRepo()
        val viewModel = SearchViewModel(repo)

        viewModel.onQueryChange("A")
        testScheduler.advanceUntilIdle()

        assertEquals("A", viewModel.uiState.value.query)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
