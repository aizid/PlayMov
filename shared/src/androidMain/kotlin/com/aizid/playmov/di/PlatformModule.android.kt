package com.aizid.playmov.di

import com.aizid.playmov.data.db.AndroidDatabaseDriverFactory
import com.aizid.playmov.data.db.DatabaseDriverFactory
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
    single { KSafe(get()) }
}
