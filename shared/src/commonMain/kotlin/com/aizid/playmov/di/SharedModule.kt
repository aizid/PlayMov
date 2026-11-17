package com.aizid.playmov.di

import com.aizid.playmov.data.pref.PrefManager
import com.aizid.playmov.utils.locale.LanguageManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule: Module = module {
    singleOf(::PrefManager)
    singleOf(::LanguageManager)
}