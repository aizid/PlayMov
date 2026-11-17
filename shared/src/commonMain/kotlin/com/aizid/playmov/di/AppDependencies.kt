package com.aizid.playmov.di

import org.koin.core.context.startKoin

val appModules = listOf(
    platformModule,
    databaseModule,
    useCaseModule,
    networkModule,
    datasourceModule,
    repositoryModule,
    sharedModule,
    viewModelModule,
)

fun initKoin() {
    startKoin {
        modules(appModules)
    }
}