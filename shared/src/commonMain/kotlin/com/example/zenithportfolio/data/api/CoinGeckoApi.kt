package com.example.zenithportfolio.data.api

import  com.example.zenithportfolio.data.dto.CryptoDto
import com.example.zenithportfolio.data.dto.MarketChartDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class CoinGeckoApi(private val client: HttpClient){
    private val baseUrl =  "https://api.coingecko.com/api/v3"

    suspend fun getMarkets(
        currency: String = "usd",
        limit: Int = 50
    ): List<CryptoDto>{
        return client.get("$baseUrl/coins/markets"){
            parameter("vs_currency", currency)
            parameter("order", "market_cap_desc")
            parameter("per_page", limit)
            parameter("page", 1)
            parameter("sparkline", false)
        }.body()
    }

    suspend fun getCoinById(id: String): CryptoDto{
        return client.get("$baseUrl/coins/$id").body()
    }

    suspend fun getMarketChart(
        id: String,
        days: Int = 7
    ): MarketChartDto{
        return client.get("$baseUrl/coins/$id/market_chart") {
            parameter("vs_currency", "usd")
            parameter("days", days)
        }.body()
    }
}