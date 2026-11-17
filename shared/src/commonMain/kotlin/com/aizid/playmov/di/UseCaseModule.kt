package com.aizid.playmov.di

import com.aizid.playmov.remote.interactor.GetMovieCreditsUseCase
import com.aizid.playmov.remote.interactor.GetMovieDetailsUseCase
import com.aizid.playmov.remote.interactor.GetMovieDiscoverUseCase
import com.aizid.playmov.remote.interactor.GetMovieReviewsUseCase
import com.aizid.playmov.remote.interactor.GetMovieVideosUseCase
import com.aizid.playmov.remote.interactor.GetNowPlayingMoviesUseCase
import com.aizid.playmov.remote.interactor.GetPopularMoviesUseCase
import com.aizid.playmov.remote.interactor.GetSeriesDiscoverUseCase
import com.aizid.playmov.remote.interactor.GetSimilarMoviesUseCase
import com.aizid.playmov.remote.interactor.GetTopRatedMoviesUseCase
import com.aizid.playmov.remote.interactor.GetUpcomingMoviesUseCase
import com.aizid.playmov.remote.interactor.SearchMoviesUseCase
import com.aizid.playmov.remote.interactor.SearchMultiUseCase
import com.aizid.playmov.remote.interactor.SearchSeriesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {

    // Watchlist
    factoryOf(::GetMovieDiscoverUseCase)

    // Order
    factoryOf(::GetSeriesDiscoverUseCase)

    // Market
    factoryOf(::GetMovieDetailsUseCase)

    // Watchlist
    factoryOf(::GetMovieReviewsUseCase)

    // Order
    factoryOf(::GetMovieVideosUseCase)

    // Market
    factoryOf(::GetMovieCreditsUseCase)


    // Watchlist
    factoryOf(::GetSimilarMoviesUseCase)

    // Order
    factoryOf(::SearchMoviesUseCase)

    // Market
    factoryOf(::SearchSeriesUseCase)

    // Watchlist
    factoryOf(::SearchMultiUseCase)

    // Order
    factoryOf(::GetNowPlayingMoviesUseCase)

    // Market
    factoryOf(::GetPopularMoviesUseCase)

    // Order
    factoryOf(::GetTopRatedMoviesUseCase)

    // Market
    factoryOf(::GetUpcomingMoviesUseCase)
}