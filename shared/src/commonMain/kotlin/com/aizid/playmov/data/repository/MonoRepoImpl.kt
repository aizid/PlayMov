package com.aizid.playmov.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.aizid.playmov.data.db.AppDatabase
import com.aizid.playmov.data.db.FavoriteMovie
import com.aizid.playmov.domain.repository.MonoRepo
import com.aizid.playmov.remote.datasource.MonoDataSource
import com.aizid.playmov.remote.dto.*
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MonoRepoImpl(
    private val remoteSource: MonoDataSource,
    private val database: AppDatabase? = null
) : MonoRepo {

    private val favoriteQueries get() = database?.favoriteMovieQueries
        ?: throw IllegalStateException("Database not provided")

    override suspend fun getGenreMovieList(): Flow<Resource<GenreResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getGenreMovieList()
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getGenreSeriesList(): Flow<Resource<GenreResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getGenreSeriesList()
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getMovieDiscover(
        genreId: Int?,
        page: Int
    ): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getMovieDiscover(genreId, page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getSeriesDiscover(
        genreId: Int?,
        page: Int
    ): Flow<Resource<TvResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getSeriesDiscover(genreId, page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetail?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getMovieDetails(movieId)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getMovieReviews(
        movieId: Int,
        page: Int
    ): Flow<Resource<ReviewResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getMovieReviews(movieId, page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getMovieVideos(movieId: Int): Flow<Resource<VideoResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getMovieVideos(movieId)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getMovieCredits(movieId: Int): Flow<Resource<CreditsResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getMovieCredits(movieId)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getSimilarMovies(
        movieId: Int,
        page: Int
    ): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getSimilarMovies(movieId, page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun searchMovies(
        query: String,
        page: Int
    ): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.searchMovies(query, page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun searchSeries(
        query: String,
        page: Int
    ): Flow<Resource<TvResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.searchSeries(query, page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun searchMulti(
        query: String,
        page: Int
    ): Flow<Resource<MultiSearchResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.searchMulti(query, page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getNowPlayingMovies(page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getNowPlayingMovies(page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getPopularMovies(page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getPopularMovies(page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getTopRatedMovies(page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getTopRatedMovies(page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    override suspend fun getUpcomingMovies(page: Int): Flow<Resource<MovieResponse?>> = flow {
        emit(Resource.Loading)
        val response = remoteSource.getUpcomingMovies(page)
        emit(Resource.Success(data = response, DataSource.REMOTE))
    }

    // --- SQLDelight Local Favorite Operations ---

    override fun getAllFavoriteMovies(): Flow<List<FavoriteMovie>> {
        return favoriteQueries.selectAllFavorites().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun isFavoriteMovie(movieId: Int): Boolean = withContext(Dispatchers.IO) {
        favoriteQueries.selectFavoriteById(movieId.toLong()).executeAsOneOrNull() != null
    }

    override suspend fun toggleFavoriteMovie(movie: MovieDetail): Unit = withContext(Dispatchers.IO) {
        val isFav = isFavoriteMovie(movie.id)
        if (isFav) {
            favoriteQueries.deleteFavorite(movie.id.toLong())
        } else {
            favoriteQueries.insertFavorite(
                id = movie.id.toLong(),
                title = movie.title,
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage,
                releaseDate = movie.releaseDate
            )
        }
    }
}
