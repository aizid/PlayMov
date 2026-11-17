package com.aizid.playmov.ui.movies

import com.aizid.playmov.remote.dto.MovieDetail
import com.aizid.playmov.remote.dto.Video
import com.aizid.playmov.remote.dto.VideoResponse
import com.aizid.playmov.testutil.FakeMonoRepo
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.FailureData
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

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
    fun loadMovieDetails_fetchesDetailsAndVideos() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            movieDetailsResult = Resource.Success(
                MovieDetail(id = 550, title = "Fight Club", overview = "A ticking-time-bomb insomniac..."),
                DataSource.REMOTE
            )
            movieVideosResult = Resource.Success(
                VideoResponse(550, listOf(Video("en", "US", "Trailer", "yt_video_key_1", "YouTube", 1080, "Trailer", true, "", "v1"))),
                DataSource.REMOTE
            )
            isFavoriteStatus = false
        }

        val viewModel = MovieDetailViewModel(repo)
        viewModel.loadMovieDetails(550)
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Fight Club", viewModel.uiState.value.movieDetail?.title)
        assertEquals(1, viewModel.uiState.value.videos.size)
        assertEquals("yt_video_key_1", viewModel.uiState.value.videos[0].key)
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun onToggleFavorite_updatesFavoriteState() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            movieDetailsResult = Resource.Success(
                MovieDetail(id = 550, title = "Fight Club"),
                DataSource.REMOTE
            )
            isFavoriteStatus = false
        }

        val viewModel = MovieDetailViewModel(repo)
        viewModel.loadMovieDetails(550)
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFavorite)

        viewModel.onToggleFavorite()
        testScheduler.advanceUntilIdle()

        assertTrue(repo.toggledFavorite)
        assertTrue(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun loadMovieDetails_setsErrorMessage_onFailure() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            movieDetailsResult = Resource.Failure(FailureData(404, "Movie not found"))
        }

        val viewModel = MovieDetailViewModel(repo)
        viewModel.loadMovieDetails(99999)
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Movie not found", viewModel.uiState.value.errorMessage)
    }
}
