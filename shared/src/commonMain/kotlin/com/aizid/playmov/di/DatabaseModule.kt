package com.aizid.playmov.di

import com.aizid.playmov.data.db.AppDatabase
import com.aizid.playmov.data.db.DatabaseDriverFactory
import org.koin.dsl.module

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        AppDatabase(driverFactory.createDriver())
    }
}