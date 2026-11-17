package com.aizid.playmov.remote.datasource

import com.aizid.playmov.remote.dto.CreditsResponse
import com.aizid.playmov.remote.dto.GenreResponse
import com.aizid.playmov.remote.dto.MovieDetail
import com.aizid.playmov.remote.dto.MovieResponse
import com.aizid.playmov.remote.dto.MultiSearchResponse
import com.aizid.playmov.remote.dto.ReviewResponse
import com.aizid.playmov.remote.dto.TvResponse
import com.aizid.playmov.remote.dto.VideoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MonoDataSourceImpl(
    private val client: HttpClient
) : MonoDataSource {

    // 1. Genre
    override suspend fun getGenreMovieList(): GenreResponse? {
        return client.get("genre/movie/list").body()
    }

    override suspend fun getGenreSeriesList(): GenreResponse? {
        return client.get("genre/tv/list").body()
    }

    // 2. Discover
    override suspend fun getMovieDiscover(genreId: Int?, page: Int): MovieResponse? {
        return client.get("discover/movie") {
            genreId?.let { parameter("with_genres", it) }
            parameter("page", page)
        }.body()
    }

    override suspend fun getSeriesDiscover(genreId: Int?, page: Int): TvResponse? {
        return client.get("discover/tv") {
            genreId?.let { parameter("with_genres", it) }
            parameter("page", page)
        }.body()
    }

    // 3. Movies Detail, Reviews, Videos, Credits, Similar
    override suspend fun getMovieDetails(movieId: Int): MovieDetail? {
        return client.get("movie/$movieId").body()
    }

    override suspend fun getMovieReviews(movieId: Int, page: Int): ReviewResponse? {
        return client.get("movie/$movieId/reviews") {
            parameter("page", page)
        }.body()
    }

    override suspend fun getMovieVideos(movieId: Int): VideoResponse? {
        return client.get("movie/$movieId/videos").body()
    }

    override suspend fun getMovieCredits(movieId: Int): CreditsResponse? {
        return client.get("movie/$movieId/credits").body()
    }

    override suspend fun getSimilarMovies(movieId: Int, page: Int): MovieResponse? {
        return client.get("movie/$movieId/similar") {
            parameter("page", page)
        }.body()
    }

    // 4. Search
    override suspend fun searchMovies(query: String, page: Int): MovieResponse? {
        return client.get("search/movie") {
            parameter("query", query)
            parameter("page", page)
        }.body()
    }

    override suspend fun searchSeries(query: String, page: Int): TvResponse? {
        return client.get("search/tv") {
            parameter("query", query)
            parameter("page", page)
        }.body()
    }

    override suspend fun searchMulti(query: String, page: Int): MultiSearchResponse? {
        return client.get("search/multi") {
            parameter("query", query)
            parameter("page", page)
        }.body()
    }

    // 5. Movie Lists
    override suspend fun getNowPlayingMovies(page: Int): MovieResponse? {
        return client.get("movie/now_playing") {
            parameter("page", page)
        }.body()
    }

    override suspend fun getPopularMovies(page: Int): MovieResponse? {
        return client.get("movie/popular") {
            parameter("page", page)
        }.body()
    }

    override suspend fun getTopRatedMovies(page: Int): MovieResponse? {
        return client.get("movie/top_rated") {
            parameter("page", page)
        }.body()
    }

    override suspend fun getUpcomingMovies(page: Int): MovieResponse? {
        return client.get("movie/upcoming") {
            parameter("page", page)
        }.body()
    }
}