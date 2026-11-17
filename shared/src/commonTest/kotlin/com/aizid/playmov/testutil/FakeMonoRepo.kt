package com.aizid.playmov.testutil

import com.aizid.playmov.data.db.FavoriteMovie
import com.aizid.playmov.domain.repository.MonoRepo
import com.aizid.playmov.remote.dto.*
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.FailureData
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMonoRepo : MonoRepo {
    var genreMovieListResult: Resource<GenreResponse?> = Resource.Success(
        GenreResponse(listOf(Genre(28, "Action"), Genre(12, "Adventure"))),
        DataSource.REMOTE
    )
    var movieDiscoverResult: Resource<MovieResponse?> = Resource.Success(
        MovieResponse(1, listOf(Movie(id = 550, title = "Fight Club")), 5, 50),
        DataSource.REMOTE
    )
    var movieDetailsResult: Resource<MovieDetail?> = Resource.Success(
        MovieDetail(id = 550, title = "Fight Club", voteAverage = 8.4),
        DataSource.REMOTE
    )
    var movieVideosResult: Resource<VideoResponse?> = Resource.Success(
        VideoResponse(550, listOf(Video("en", "US", "Trailer", "key_xyz", "YouTube", 1080, "Trailer", true, "", "id_1"))),
        DataSource.REMOTE
    )
    var movieCreditsResult: Resource<CreditsResponse?> = Resource.Success(
        CreditsResponse(550, listOf(Cast(1, "Brad Pitt", "Tyler Durden", null))),
        DataSource.REMOTE
    )
    var movieReviewsResult: Resource<ReviewResponse?> = Resource.Success(
        ReviewResponse(550, 1, emptyList(), 1, 0),
        DataSource.REMOTE
    )
    var searchMoviesResult: Resource<MovieResponse?> = Resource.Success(
        MovieResponse(1, listOf(Movie(id = 550, title = "Fight Club")), 1, 1),
        DataSource.REMOTE
    )

    var isFavoriteStatus = false
    var toggledFavorite = false

    override suspend fun getGenreMovieList(): Flow<Resource<GenreResponse?>> = flow {
        emit(Resource.Loading)
        emit(genreMovieListResult)
    }

    override suspend fun getGenreSeriesList(): Flow<Resource<GenreResponse?>> = flow {
        emit(Resource.Loading)
        emit(genreMovieListResult)
    }

    override suspend fun getMovieDiscover(genreId: Int?, page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        emit(movieDiscoverResult)
    }

    override suspend fun getSeriesDiscover(genreId: Int?, page: Int): Flow<Resource<TvResponse?>> = flow {
        emit(Resource.Loading)
    }

    override suspend fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetail?>> = flow {
        emit(Resource.Loading)
        emit(movieDetailsResult)
    }

    override suspend fun getMovieReviews(movieId: Int, page: Int): Flow<Resource<ReviewResponse?>> = flow {
        emit(Resource.Loading)
        emit(movieReviewsResult)
    }

    override suspend fun getMovieVideos(movieId: Int): Flow<Resource<VideoResponse?>> = flow {
        emit(Resource.Loading)
        emit(movieVideosResult)
    }

    override suspend fun getMovieCredits(movieId: Int): Flow<Resource<CreditsResponse?>> = flow {
        emit(Resource.Loading)
        emit(movieCreditsResult)
    }

    override suspend fun getSimilarMovies(movieId: Int, page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
    }

    override suspend fun searchMovies(query: String, page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        emit(searchMoviesResult)
    }

    override suspend fun searchSeries(query: String, page: Int): Flow<Resource<TvResponse?>> = flow {
        emit(Resource.Loading)
    }

    override suspend fun searchMulti(query: String, page: Int): Flow<Resource<MultiSearchResponse?>> = flow {
        emit(Resource.Loading)
    }

    override suspend fun getNowPlayingMovies(page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
    }

    override suspend fun getPopularMovies(page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
    }

    override suspend fun getTopRatedMovies(page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
    }

    override suspend fun getUpcomingMovies(page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
    }

    override fun getAllFavoriteMovies(): Flow<List<FavoriteMovie>> = flow {
        emit(emptyList())
    }

    override suspend fun isFavoriteMovie(movieId: Int): Boolean = isFavoriteStatus

    override suspend fun toggleFavoriteMovie(movie: MovieDetail) {
        toggledFavorite = true
        isFavoriteStatus = !isFavoriteStatus
    }
}
