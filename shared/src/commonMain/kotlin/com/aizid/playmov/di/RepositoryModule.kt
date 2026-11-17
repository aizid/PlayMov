package com.aizid.playmov.di

import com.aizid.playmov.data.repository.AccountRepoImpl
import com.aizid.playmov.data.repository.MonoRepoImpl
import com.aizid.playmov.domain.repository.AccountRepo
import com.aizid.playmov.domain.repository.MonoRepo
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::MonoRepoImpl) { bind<MonoRepo>() }
    singleOf(::AccountRepoImpl) { bind<AccountRepo>() }
}