package com.aizid.playmov.ui.movie_list

import com.aizid.playmov.remote.dto.AuthorDetails
import com.aizid.playmov.remote.dto.Review
import com.aizid.playmov.remote.dto.ReviewResponse
import com.aizid.playmov.testutil.FakeMonoRepo
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewsViewModelTest {

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
    fun loadReviews_populatesReviewsList() = runTest(testDispatcher) {
        val repo = FakeMonoRepo().apply {
            movieReviewsResult = Resource.Success(
                ReviewResponse(
                    id = 550,
                    page = 1,
                    results = listOf(
                        Review(
                            author = "FilmCritic99",
                            authorDetails = AuthorDetails(name = "Critic", username = "critic", avatarPath = null, rating = 9.0),
                            content = "An absolute masterpiece of psychological cinema.",
                            createdAt = "2024-01-01",
                            id = "rev_1",
                            updatedAt = "2024-01-01",
                            url = "https://tmdb.org/review/rev_1"
                        )
                    ),
                    totalPages = 1,
                    totalResults = 1
                ),
                DataSource.REMOTE
            )
        }

        val viewModel = ReviewsViewModel(repo)
        viewModel.loadReviews(550)
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.reviews.size)
        assertEquals("FilmCritic99", viewModel.uiState.value.reviews[0].author)
    }
}
