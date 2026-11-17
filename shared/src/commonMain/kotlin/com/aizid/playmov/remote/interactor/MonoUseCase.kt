package com.aizid.playmov.remote.interactor

import com.aizid.playmov.remote.dto.CreditsResponse
import com.aizid.playmov.remote.dto.GenreResponse
import com.aizid.playmov.remote.dto.IntsRequest
import com.aizid.playmov.remote.dto.MovieDetail
import com.aizid.playmov.remote.dto.MovieResponse
import com.aizid.playmov.remote.dto.MultiSearchResponse
import com.aizid.playmov.remote.dto.ReviewResponse
import com.aizid.playmov.remote.dto.StrIntRequest
import com.aizid.playmov.remote.dto.TvResponse
import com.aizid.playmov.remote.dto.VideoResponse
import com.aizid.playmov.domain.repository.MonoRepo
import com.aizid.playmov.utils.FlowUseCase
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.Flow

class GetGenreMovieListUseCase(
    private val repository: MonoRepo
) : FlowUseCase<Unit, GenreResponse?>() {
    override suspend fun execute(parameters: Unit?): Flow<Resource<GenreResponse?>> =
        repository.getGenreMovieList()
}

class GetGenreSeriesListUseCase(
    private val repository: MonoRepo
) : FlowUseCase<Unit, GenreResponse?>() {
    override suspend fun execute(parameters: Unit?): Flow<Resource<GenreResponse?>> =
        repository.getGenreSeriesList()
}

class GetMovieDiscoverUseCase constructor(
    private val repository: MonoRepo
) : FlowUseCase<IntsRequest?, MovieResponse?>() {
    override suspend fun execute(parameters: IntsRequest?): Flow<Resource<MovieResponse?>> =
        repository.getMovieDiscover(parameters!!.valueId, parameters.page!!)
}

class GetSeriesDiscoverUseCase(
    private val repository: MonoRepo
) : FlowUseCase<IntsRequest?, TvResponse?>() {
    override suspend fun execute(parameters: IntsRequest?): Flow<Resource<TvResponse?>> =
        repository.getSeriesDiscover(parameters?.valueId, parameters!!.page!!)
}

class GetMovieDetailsUseCase(
    private val repository: MonoRepo
) : FlowUseCase<Int, MovieDetail?>() {
    override suspend fun execute(parameters: Int?): Flow<Resource<MovieDetail?>> =
        repository.getMovieDetails(parameters!!)
}

class GetMovieReviewsUseCase(
    private val repository: MonoRepo
) : FlowUseCase<IntsRequest?, ReviewResponse?>() {
    override suspend fun execute(parameters: IntsRequest?): Flow<Resource<ReviewResponse?>> =
        repository.getMovieReviews(parameters!!.valueId!!, parameters.page!!)
}

class GetMovieVideosUseCase(
    private val repository: MonoRepo
) : FlowUseCase<Int, VideoResponse?>() {
    override suspend fun execute(parameters: Int?): Flow<Resource<VideoResponse?>> =
        repository.getMovieVideos(parameters!!)
}

class GetMovieCreditsUseCase(
    private val repository: MonoRepo
) : FlowUseCase<Int, CreditsResponse?>() {
    override suspend fun execute(parameters: Int?): Flow<Resource<CreditsResponse?>> =
        repository.getMovieCredits(parameters!!)
}

class GetSimilarMoviesUseCase(
    private val repository: MonoRepo
) : FlowUseCase<IntsRequest?, MovieResponse?>() {
    override suspend fun execute(parameters: IntsRequest?): Flow<Resource<MovieResponse?>> =
        repository.getSimilarMovies(parameters!!.valueId!!, parameters.page!!)
}

class SearchMoviesUseCase(
    private val repository: MonoRepo
) : FlowUseCase<StrIntRequest?, MovieResponse?>() {
    override suspend fun execute(parameters: StrIntRequest?): Flow<Resource<MovieResponse?>> =
        repository.searchMovies(parameters!!.valueQuery!!, parameters.page!!)
}

class SearchSeriesUseCase(
    private val repository: MonoRepo
) : FlowUseCase<StrIntRequest?, TvResponse?>() {
    override suspend fun execute(parameters: StrIntRequest?): Flow<Resource<TvResponse?>> =
        repository.searchSeries(parameters!!.valueQuery!!, parameters.page!!)
}

class SearchMultiUseCase(
    private val repository: MonoRepo
) : FlowUseCase<StrIntRequest?, MultiSearchResponse?>() {
    override suspend fun execute(parameters: StrIntRequest?): Flow<Resource<MultiSearchResponse?>> =
        repository.searchMulti(parameters!!.valueQuery!!, parameters.page!!)
}

class GetNowPlayingMoviesUseCase(private val repository: MonoRepo) : FlowUseCase<Int, MovieResponse?>() {
    override suspend fun execute(parameters: Int?): Flow<Resource<MovieResponse?>> = repository.getNowPlayingMovies(parameters ?: 1)
}

class GetPopularMoviesUseCase(private val repository: MonoRepo) : FlowUseCase<Int, MovieResponse?>() {
    override suspend fun execute(parameters: Int?): Flow<Resource<MovieResponse?>> = repository.getPopularMovies(parameters ?: 1)
}

class GetTopRatedMoviesUseCase(private val repository: MonoRepo) : FlowUseCase<Int, MovieResponse?>() {
    override suspend fun execute(parameters: Int?): Flow<Resource<MovieResponse?>> = repository.getTopRatedMovies(parameters ?: 1)
}

class GetUpcomingMoviesUseCase(private val repository: MonoRepo) : FlowUseCase<Int, MovieResponse?>() {
    override suspend fun execute(parameters: Int?): Flow<Resource<MovieResponse?>> = repository.getUpcomingMovies(parameters ?: 1)
}
