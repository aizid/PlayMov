package com.aizid.playmov.data.repository

import com.aizid.playmov.remote.datasource.MonoDataSource
import com.aizid.playmov.remote.dto.*
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeMonoDataSource : MonoDataSource {
    var genreListResult: GenreResponse? = GenreResponse(listOf(Genre(28, "Action")))
    var discoverMovieResult: MovieResponse? = MovieResponse(1, emptyList(), 1, 0)
    var movieDetailResult: MovieDetail? = MovieDetail(id = 550, title = "Fight Club")
    var movieVideosResult: VideoResponse? = VideoResponse(550, listOf(Video("en", "US", "Trailer", "key_123", "YouTube", 1080, "Trailer", true, "", "id1")))
    var movieReviewsResult: ReviewResponse? = ReviewResponse(550, 1, emptyList(), 1, 0)
    var multiSearchResult: MultiSearchResponse? = MultiSearchResponse(1, emptyList(), 1, 0)

    override suspend fun getGenreMovieList(): GenreResponse? = genreListResult
    override suspend fun getGenreSeriesList(): GenreResponse? = genreListResult
    override suspend fun getMovieDiscover(genreId: Int?, page: Int): MovieResponse? = discoverMovieResult
    override suspend fun getSeriesDiscover(genreId: Int?, page: Int): TvResponse? = null
    override suspend fun getMovieDetails(movieId: Int): MovieDetail? = movieDetailResult
    override suspend fun getMovieReviews(movieId: Int, page: Int): ReviewResponse? = movieReviewsResult
    override suspend fun getMovieVideos(movieId: Int): VideoResponse? = movieVideosResult
    override suspend fun getMovieCredits(movieId: Int): CreditsResponse? = null
    override suspend fun getSimilarMovies(movieId: Int, page: Int): MovieResponse? = null
    override suspend fun searchMovies(query: String, page: Int): MovieResponse? = null
    override suspend fun searchSeries(query: String, page: Int): TvResponse? = null
    override suspend fun searchMulti(query: String, page: Int): MultiSearchResponse? = multiSearchResult
    override suspend fun getNowPlayingMovies(page: Int): MovieResponse? = null
    override suspend fun getPopularMovies(page: Int): MovieResponse? = null
    override suspend fun getTopRatedMovies(page: Int): MovieResponse? = null
    override suspend fun getUpcomingMovies(page: Int): MovieResponse? = null
}

class MonoRepoTest {

    @Test
    fun getGenreMovieList_emitsLoadingThenSuccess() = runTest {
        val fakeSource = FakeMonoDataSource()
        val repo = MonoRepoImpl(fakeSource)

        val emissions = repo.getGenreMovieList().toList()
        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        val data = (emissions[1] as Resource.Success).data
        assertEquals(1, data?.genres?.size)
        assertEquals("Action", data?.genres?.first()?.name)
    }

    @Test
    fun getMovieDiscover_emitsLoadingThenSuccess() = runTest {
        val fakeSource = FakeMonoDataSource()
        val repo = MonoRepoImpl(fakeSource)

        val emissions = repo.getMovieDiscover(genreId = 28, page = 1).toList()
        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
    }

    @Test
    fun getMovieDetails_returnsRequestedMovie() = runTest {
        val fakeSource = FakeMonoDataSource()
        val repo = MonoRepoImpl(fakeSource)

        val emissions = repo.getMovieDetails(550).toList()
        assertTrue(emissions.last() is Resource.Success)
        val detail = (emissions.last() as Resource.Success).data
        assertEquals(550, detail?.id)
        assertEquals("Fight Club", detail?.title)
    }

    @Test
    fun getMovieVideos_returnsTrailers() = runTest {
        val fakeSource = FakeMonoDataSource()
        val repo = MonoRepoImpl(fakeSource)

        val emissions = repo.getMovieVideos(550).toList()
        assertTrue(emissions.last() is Resource.Success)
        val videos = (emissions.last() as Resource.Success).data
        assertEquals("key_123", videos?.results?.first()?.key)
    }

    @Test
    fun searchMulti_emitsSearchResults() = runTest {
        val fakeSource = FakeMonoDataSource()
        val repo = MonoRepoImpl(fakeSource)

        val emissions = repo.searchMulti("Batman", 1).toList()
        assertTrue(emissions.last() is Resource.Success)
    }
}
