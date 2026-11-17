package com.aizid.playmov.di

import com.aizid.playmov.data.db.DatabaseDriverFactory
import com.aizid.playmov.data.db.IOSDatabaseDriverFactory
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
    single { KSafe() }
}
