package com.aizid.playmov.di

import com.aizid.playmov.ui.auth.login.LoginViewModel
import com.aizid.playmov.ui.auth.otp.OtpViewModel
import com.aizid.playmov.ui.auth.register.RegisterViewModel
import com.aizid.playmov.ui.discover.DiscoverViewModel
import com.aizid.playmov.ui.genre.GenreViewModel
import com.aizid.playmov.ui.movie_list.ReviewsViewModel
import com.aizid.playmov.ui.movies.MovieDetailViewModel
import com.aizid.playmov.ui.profile.ProfileViewModel
import com.aizid.playmov.ui.search.SearchViewModel
import com.aizid.playmov.ui.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::GenreViewModel)
    viewModelOf(::DiscoverViewModel)
    viewModelOf(::MovieDetailViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::ReviewsViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::OtpViewModel)
    viewModelOf(::ProfileViewModel)
}