package com.aizid.playmov.di

import com.aizid.playmov.remote.datasource.AccountDataSource
import com.aizid.playmov.remote.datasource.AccountDataSourceImpl
import com.aizid.playmov.remote.datasource.MonoDataSource
import com.aizid.playmov.remote.datasource.MonoDataSourceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val datasourceModule = module {
    singleOf(::MonoDataSourceImpl) { bind<MonoDataSource>() }
    single<AccountDataSource> { AccountDataSourceImpl(client = get(named("graphqlClient"))) }
}