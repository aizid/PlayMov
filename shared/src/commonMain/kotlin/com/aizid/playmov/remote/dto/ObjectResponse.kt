package com.aizid.playmov.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 1. Genre Response
@Serializable
data class GenreResponse(
    @SerialName("genres") val genres: List<Genre>
)

@Serializable
data class Genre(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

// 2. Discover / Movie List Response (Pagination)
@Serializable
data class MovieResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<Movie>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

@Serializable
data class Movie(
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList(),
    @SerialName("id") val id: Int = 0,
    @SerialName("original_language") val originalLanguage: String = "en",
    @SerialName("original_title") val originalTitle: String = "",
    @SerialName("overview") val overview: String = "",
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("title") val title: String = "",
    @SerialName("video") val video: Boolean = false,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0
)

// 3. Movie Detail Response
@Serializable
data class MovieDetail(
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("budget") val budget: Long = 0L,
    @SerialName("genres") val genres: List<Genre> = emptyList(),
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("id") val id: Int = 0,
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("origin_country") val originCountry: List<String>? = null,
    @SerialName("original_language") val originalLanguage: String = "en",
    @SerialName("original_title") val originalTitle: String = "",
    @SerialName("overview") val overview: String? = null,
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("revenue") val revenue: Long = 0L,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("status") val status: String = "",
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("title") val title: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0
)

// 4. TV Show Response (Pagination untuk Discover TV / Search TV)
@Serializable
data class TvResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<TvShow>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

@Serializable
data class TvShow(
    @SerialName("adult") val adult: Boolean,
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("genre_ids") val genreIds: List<Int>,
    @SerialName("id") val id: Int,
    @SerialName("origin_country") val originCountry: List<String>?,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("original_name") val originalName: String,
    @SerialName("overview") val overview: String,
    @SerialName("popularity") val popularity: Double,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("first_air_date") val firstAirDate: String?,
    @SerialName("name") val name: String,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int
)

// 5. Movie Reviews Response (Pagination)
@Serializable
data class ReviewResponse(
    @SerialName("id") val id: Int,
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<Review>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

@Serializable
data class Review(
    @SerialName("author") val author: String,
    @SerialName("author_details") val authorDetails: AuthorDetails,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("id") val id: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("url") val url: String
)

@Serializable
data class AuthorDetails(
    @SerialName("name") val name: String?,
    @SerialName("username") val username: String,
    @SerialName("avatar_path") val avatarPath: String?,
    @SerialName("rating") val rating: Double?
)

@Serializable
data class CreditsResponse(
    @SerialName("id") val id: Int = 0,
    @SerialName("cast") val cast: List<Cast> = emptyList(),
    @SerialName("crew") val crew: List<Crew> = emptyList()
)
@Serializable
data class Cast(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("character") val character: String,
    @SerialName("profile_path") val profilePath: String?
)
@Serializable
data class Crew(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("job") val job: String,
    @SerialName("department") val department: String
)

// 6. Movie Videos / Trailers Response
@Serializable
data class VideoResponse(
    @SerialName("id") val id: Int,
    @SerialName("results") val results: List<Video>
)

@Serializable
data class Video(
    @SerialName("iso_639_1") val iso6391: String,
    @SerialName("iso_3166_1") val iso31661: String,
    @SerialName("name") val name: String,
    @SerialName("key") val key: String, // YouTube Video ID (e.g., dQw4w9WgXcQ)
    @SerialName("site") val site: String,
    @SerialName("size") val size: Int,
    @SerialName("type") val type: String, // "Trailer", "Teaser", etc.
    @SerialName("official") val official: Boolean,
    @SerialName("published_at") val publishedAt: String,
    @SerialName("id") val id: String
)

@Serializable
data class MultiSearchResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<MultiSearchResult>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)
@Serializable
data class MultiSearchResult(
    @SerialName("id") val id: Int,
    @SerialName("media_type") val mediaType: String, // "movie", "tv", atau "person"
    @SerialName("title") val title: String? = null, // Untuk movie
    @SerialName("name") val name: String? = null,   // Untuk TV/Person
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("profile_path") val profilePath: String? = null
)