package com.erkindilekci.di

import com.erkindilekci.repository.HeroRepository
import com.erkindilekci.repository.HeroRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    single<HeroRepository> {
        HeroRepositoryImpl()
    }
}
