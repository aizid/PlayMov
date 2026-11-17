package com.aizid.playmov.remote.datasource

import com.aizid.playmov.remote.dto.CreditsResponse
import com.aizid.playmov.remote.dto.GenreResponse
import com.aizid.playmov.remote.dto.MovieDetail
import com.aizid.playmov.remote.dto.MovieResponse
import com.aizid.playmov.remote.dto.MultiSearchResponse
import com.aizid.playmov.remote.dto.ReviewResponse
import com.aizid.playmov.remote.dto.TvResponse
import com.aizid.playmov.remote.dto.VideoResponse

interface MonoDataSource {
    // 1. Genre
    suspend fun getGenreMovieList(): GenreResponse?
    suspend fun getGenreSeriesList(): GenreResponse?

    // 2. Discover
    suspend fun getMovieDiscover(genreId: Int?, page: Int): MovieResponse?
    suspend fun getSeriesDiscover(genreId: Int?, page: Int): TvResponse?

    // 3. Movies Detail, Reviews, Videos, Credits, Similar
    suspend fun getMovieDetails(movieId: Int): MovieDetail?
    suspend fun getMovieReviews(movieId: Int, page: Int): ReviewResponse?
    suspend fun getMovieVideos(movieId: Int): VideoResponse?
    suspend fun getMovieCredits(movieId: Int): CreditsResponse?
    suspend fun getSimilarMovies(movieId: Int, page: Int): MovieResponse?

    // 4. Search
    suspend fun searchMovies(query: String, page: Int): MovieResponse?
    suspend fun searchSeries(query: String, page: Int): TvResponse?
    suspend fun searchMulti(query: String, page: Int): MultiSearchResponse?

    // 5. Movie Lists
    suspend fun getNowPlayingMovies(page: Int): MovieResponse?
    suspend fun getPopularMovies(page: Int): MovieResponse?
    suspend fun getTopRatedMovies(page: Int): MovieResponse?
    suspend fun getUpcomingMovies(page: Int): MovieResponse?
}