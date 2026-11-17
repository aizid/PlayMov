package com.aizid.playmov.domain.repository

import com.aizid.playmov.data.db.FavoriteMovie
import com.aizid.playmov.remote.dto.CreditsResponse
import com.aizid.playmov.remote.dto.GenreResponse
import com.aizid.playmov.remote.dto.MovieDetail
import com.aizid.playmov.remote.dto.MovieResponse
import com.aizid.playmov.remote.dto.MultiSearchResponse
import com.aizid.playmov.remote.dto.ReviewResponse
import com.aizid.playmov.remote.dto.TvResponse
import com.aizid.playmov.remote.dto.VideoResponse
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.Flow

interface MonoRepo {
    // 1. Genre
    suspend fun getGenreMovieList(): Flow<Resource<GenreResponse?>>
    suspend fun getGenreSeriesList(): Flow<Resource<GenreResponse?>>

    // 2. Discover
    suspend fun getMovieDiscover(genreId: Int?, page: Int): Flow<Resource<MovieResponse?>>
    suspend fun getSeriesDiscover(genreId: Int?, page: Int): Flow<Resource<TvResponse?>>

    // 3. Movies Detail, Reviews, Videos, Credits, Similar
    suspend fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetail?>>
    suspend fun getMovieReviews(movieId: Int, page: Int): Flow<Resource<ReviewResponse?>>
    suspend fun getMovieVideos(movieId: Int): Flow<Resource<VideoResponse?>>
    suspend fun getMovieCredits(movieId: Int): Flow<Resource<CreditsResponse?>>
    suspend fun getSimilarMovies(movieId: Int, page: Int): Flow<Resource<MovieResponse?>>

    // 4. Search
    suspend fun searchMovies(query: String, page: Int): Flow<Resource<MovieResponse?>>
    suspend fun searchSeries(query: String, page: Int): Flow<Resource<TvResponse?>>
    suspend fun searchMulti(query: String, page: Int): Flow<Resource<MultiSearchResponse?>>

    // 5. Movie Lists
    suspend fun getNowPlayingMovies(page: Int): Flow<Resource<MovieResponse?>>
    suspend fun getPopularMovies(page: Int): Flow<Resource<MovieResponse?>>
    suspend fun getTopRatedMovies(page: Int): Flow<Resource<MovieResponse?>>
    suspend fun getUpcomingMovies(page: Int): Flow<Resource<MovieResponse?>>

    // 6. Local SQLDelight Favorites
    fun getAllFavoriteMovies(): Flow<List<FavoriteMovie>>
    suspend fun isFavoriteMovie(movieId: Int): Boolean
    suspend fun toggleFavoriteMovie(movie: MovieDetail)
}
