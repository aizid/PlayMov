package com.aizid.playmov

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.aizid.playmov.data.pref.PrefManager
import com.aizid.playmov.di.appModules
import com.aizid.playmov.routes.*
import com.aizid.playmov.ui.auth.login.LoginScreen
import com.aizid.playmov.ui.auth.otp.OtpVerificationScreen
import com.aizid.playmov.ui.auth.register.RegisterScreen
import com.aizid.playmov.ui.discover.DiscoverScreen
import com.aizid.playmov.ui.genre.GenreScreen
import com.aizid.playmov.ui.movie_list.MovieReviewsScreen
import com.aizid.playmov.ui.movie_list.TrailerPlayerScreen
import com.aizid.playmov.ui.movies.MovieDetailScreen
import com.aizid.playmov.ui.onboarding.OnboardingScreen
import com.aizid.playmov.ui.profile.ProfileScreen
import com.aizid.playmov.ui.search.SearchScreen
import com.aizid.playmov.ui.splash.SplashScreen
import com.aizid.playmov.ui.theme.PlayMovTheme
import com.aizid.playmov.utils.locale.LanguageManager
import com.aizid.playmov.utils.locale.ProvideAppLocale
import com.aizid.playmov.utils.locale.getSystemLanguage
import com.aizid.playmov.utils.toast.ToastHost
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.KoinAppDeclaration

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Splash::class)
            subclass(Onboarding::class)
            subclass(Login::class)
            subclass(Register::class)
            subclass(OtpVerification::class)
            subclass(GenreExplorer::class)
            subclass(Profile::class)
            subclass(MovieDiscover::class)
            subclass(MovieDetailRoute::class)
            subclass(TrailerPlayer::class)
            subclass(MovieReviews::class)
            subclass(MultiSearch::class)
        }
    }
}

@Composable
@Preview
fun App(koinConfiguration: KoinAppDeclaration = {}) {
    KoinApplication(
        application = {
            koinConfiguration()
            modules(appModules)
        }
    ) {
        val languageManager: LanguageManager = koinInject()
        val pref: PrefManager = koinInject()
        val coroutineScope = rememberCoroutineScope()
        val currentLanguage by languageManager.currentLanguage.collectAsState()

        LaunchedEffect(Unit) {
            val savedLanguage = pref.getLanguage()
            languageManager.setLanguage(savedLanguage.ifEmpty { getSystemLanguage() })
        }

        val backStack = rememberNavBackStack(config, Splash as Route)

        val viewModelStores = remember { mutableMapOf<NavKey, ViewModelStore>() }
        val backStackList = backStack.toList()
        LaunchedEffect(backStackList) {
            val keysToRemove = viewModelStores.keys.filter { it !in backStackList }
            keysToRemove.forEach { key ->
                viewModelStores[key]?.clear()
                viewModelStores.remove(key)
            }
        }

        ProvideAppLocale(currentLanguage) {
            PlayMovTheme {
                NavDisplay(backStack = backStack) { key ->
                    NavEntry(key) {
                        val viewModelStore = viewModelStores.getOrPut(it) { ViewModelStore() }
                        CompositionLocalProvider(
                            LocalViewModelStoreOwner provides remember(viewModelStore) {
                                object : ViewModelStoreOwner {
                                    override val viewModelStore: ViewModelStore = viewModelStore
                                }
                            }
                        ) {
                            when (val route = it) {
                                is Splash -> SplashScreen(
                                    onNavigateToOnboarding = {
                                        backStack.clear()
                                        backStack.add(Onboarding)
                                    },
                                    onNavigateToGenre = {
                                        backStack.clear()
                                        backStack.add(GenreExplorer)
                                    }
                                )
                                is Onboarding -> {
                                    LaunchedEffect(Unit) {
                                        pref.setOnboardingCompleted(true)
                                    }
                                    OnboardingScreen(
                                        onGetStartedClick = {
                                            coroutineScope.launch { pref.setOnboardingCompleted(true) }
                                            backStack.clear()
                                            backStack.add(GenreExplorer)
                                        },
                                        onSignInClick = {
                                            coroutineScope.launch { pref.setOnboardingCompleted(true) }
                                            backStack.add(Login)
                                        },
                                        onSkipClick = {
                                            coroutineScope.launch { pref.setOnboardingCompleted(true) }
                                            backStack.clear()
                                            backStack.add(GenreExplorer)
                                        }
                                    )
                                }
                                is Login -> LoginScreen(
                                    onLoginSuccess = {
                                        backStack.clear()
                                        backStack.add(GenreExplorer)
                                    },
                                    onNavigateToRegister = {
                                        backStack.add(Register())
                                    },
                                    onBackClick = {
                                        if (backStack.size > 1) {
                                            backStack.remove(route)
                                        } else {
                                            backStack.clear()
                                            backStack.add(GenreExplorer)
                                        }
                                    }
                                )
                                is Register -> RegisterScreen(
                                    onRegisterSuccess = { email, phoneNumber, name, registrationToken ->
                                        backStack.add(
                                            OtpVerification(
                                                email = email,
                                                phoneNumber = phoneNumber,
                                                name = name,
                                                registrationToken = registrationToken
                                            )
                                        )
                                    },
                                    onNavigateToLogin = {
                                        backStack.remove(route)
                                    },
                                    onBackClick = {
                                        backStack.remove(route)
                                    }
                                )
                                is OtpVerification -> OtpVerificationScreen(
                                    email = route.email,
                                    phoneNumber = route.phoneNumber,
                                    name = route.name,
                                    registrationToken = route.registrationToken,
                                    onVerificationSuccess = {
                                        backStack.clear()
                                        backStack.add(Login)
                                    },
                                    onBackClick = {
                                        backStack.remove(route)
                                    }
                                )
                                is GenreExplorer -> GenreScreen(
                                    onGenreClick = { genre ->
                                        backStack.add(
                                            MovieDiscover(
                                                genreId = genre.id,
                                                genreName = genre.name
                                            )
                                        )
                                    },
                                    onSearchClick = {
                                        backStack.add(MultiSearch)
                                    },
                                    onUserClick = {
                                        coroutineScope.launch {
                                            if (pref.isLoggedIn()) {
                                                backStack.add(Profile)
                                            } else {
                                                backStack.add(Login)
                                            }
                                        }
                                    }
                                )
                                is Profile -> ProfileScreen(
                                    onBackClick = {
                                        backStack.remove(route)
                                    },
                                    onLogoutSuccess = {
                                        backStack.clear()
                                        backStack.add(GenreExplorer)
                                    }
                                )
                                is MovieDiscover -> DiscoverScreen(
                                    genreId = route.genreId,
                                    genreName = route.genreName,
                                    onMovieClick = { movie ->
                                        backStack.add(MovieDetailRoute(movieId = movie.id))
                                    },
                                    onBackClick = {
                                        backStack.remove(route)
                                    }
                                )
                                is MovieDetailRoute -> MovieDetailScreen(
                                    movieId = route.movieId,
                                    onWatchTrailerClick = { videoKey, title ->
                                        backStack.add(TrailerPlayer(videoKey = videoKey, title = title))
                                    },
                                    onReviewsClick = { movieId, title ->
                                        backStack.add(MovieReviews(movieId = movieId, movieTitle = title))
                                    },
                                    onBackClick = {
                                        backStack.remove(route)
                                    }
                                )
                                is TrailerPlayer -> TrailerPlayerScreen(
                                    videoKey = route.videoKey,
                                    title = route.title,
                                    onBackClick = {
                                        backStack.remove(route)
                                    }
                                )
                                is MovieReviews -> MovieReviewsScreen(
                                    movieId = route.movieId,
                                    movieTitle = route.movieTitle,
                                    onBackClick = {
                                        backStack.remove(route)
                                    }
                                )
                                is MultiSearch -> SearchScreen(
                                    onMovieClick = { movie ->
                                        backStack.add(MovieDetailRoute(movieId = movie.id))
                                    },
                                    onBackClick = {
                                        backStack.remove(route)
                                    }
                                )
                            }
                        }
                    }
                }
                ToastHost()
            }
        }
    }
}
