package com.example.zenithportfolio.di

import com.example.zenithportfolio.data.api.CoinGeckoApi
import com.example.zenithportfolio.data.api.createHttpClient
import com.example.zenithportfolio.data.db.DatabaseDriverFactory
import com.example.zenithportfolio.data.repository.CryptoRepositoryImpl
import com.example.zenithportfolio.data.repository.FavoriteRepository
import com.example.zenithportfolio.db.AppDatabase
import com.example.zenithportfolio.presentation.crypto.CryptoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object IosHelper {
    fun createViewModel(): CryptoViewModel {
        val httpClient = createHttpClient()
        val api = CoinGeckoApi(httpClient)
        val repository = CryptoRepositoryImpl(api)

        val driverFactory = DatabaseDriverFactory()
        val database = AppDatabase(driverFactory.createDriver())
        val favoriteRepository = FavoriteRepository(database.favoriteQueries)

        return CryptoViewModel(repository, favoriteRepository)
    }

    fun createApi(): CoinGeckoApi {
        return CoinGeckoApi(createHttpClient())
    }
}

class ChartFetcher {
    private val api = CoinGeckoApi(createHttpClient())
    private val scope = CoroutineScope(Dispatchers.Main)

    fun fetchChart(
        cryptoId: String,
        days: Int,
        onSuccess: (List<Double>) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                val chart = api.getMarketChart(cryptoId, days)
                val prices = chart.prices.map { it[1] }
                onSuccess(prices)
            } catch (e: Exception) {
                onError(e.message ?: "Error loading chart")
            }
        }
    }
}
