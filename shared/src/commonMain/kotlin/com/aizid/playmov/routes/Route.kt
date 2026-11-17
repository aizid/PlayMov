package com.aizid.playmov.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey

@Serializable
data object Splash : Route

@Serializable
data object GenreExplorer : Route

@Serializable
data class MovieDiscover(
    val genreId: Int? = null,
    val genreName: String? = "Discover Movies"
) : Route

@Serializable
data class MovieDetailRoute(
    val movieId: Int
) : Route

@Serializable
data class TrailerPlayer(
    val videoKey: String,
    val title: String
) : Route

@Serializable
data class MovieReviews(
    val movieId: Int,
    val movieTitle: String
) : Route

@Serializable
data object MultiSearch : Route

@Serializable
data object Profile : Route

@Serializable
data object Onboarding : Route

@Serializable
data object Login : Route

@Serializable
data class Register(
    val email: String? = null
) : Route

@Serializable
data class OtpVerification(
    val email: String,
    val phoneNumber: String = "",
    val name: String = "",
    val registrationToken: String = ""
) : Route