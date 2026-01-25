package com.example.zenithportfolio.di

import com.example.zenithportfolio.data.api.CoinGeckoApi
import com.example.zenithportfolio.data.api.createHttpClient
import com.example.zenithportfolio.data.repository.CryptoRepositoryImpl
import com.example.zenithportfolio.domain.repository.CryptoRepository
import com.example.zenithportfolio.presentation.crypto.CryptoViewModel
import com.example.zenithportfolio.data.db.DatabaseDriverFactory
import com.example.zenithportfolio.data.repository.CryptoCache
import com.example.zenithportfolio.data.repository.FavoriteRepository
import com.example.zenithportfolio.db.AppDatabase


import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient() }
    single { CoinGeckoApi(get()) }
}

val databaseModule = module {
    single { get<DatabaseDriverFactory>().createDriver() }
    single { AppDatabase(get()) }
    single { get<AppDatabase>().favoriteQueries }
    single { FavoriteRepository(get()) }
    single { CryptoCache(get<AppDatabase>().cachedCryptoQueries) }
}

val viewModelModule = module {
    factory { CryptoViewModel(get(), get()) }
}

val repositoryModule = module {
    single<CryptoRepository> { CryptoRepositoryImpl(get(), get()) }
}
val appModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    viewModelModule
)

