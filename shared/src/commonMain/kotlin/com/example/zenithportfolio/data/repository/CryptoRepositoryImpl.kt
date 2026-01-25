package com.example.zenithportfolio.data.repository

import com.example.zenithportfolio.data.api.CoinGeckoApi
import com.example.zenithportfolio.data.dto.toDomain
import com.example.zenithportfolio.domain.model.Crypto
import com.example.zenithportfolio.domain.repository.CryptoRepository

class CryptoRepositoryImpl(
    private val api: CoinGeckoApi,
    private val cache: CryptoCache
): CryptoRepository{

    override suspend fun getCryptos(): Result<CryptoResult> {
        return try{
            println("[Repository] Calling CoinGecko API...")
            val response = api.getMarkets()
            val cryptos = response.map { it.toDomain() }
            cache.saveCryptos(cryptos)
            Result.success(CryptoResult(cryptos, fromCache = false))
        }catch (e: Exception){
            println("[Repository] API Error: ${e.message}")
            val cached = cache.getCachedCryptos()
            if (cached.isNotEmpty()) {
                println("[Repository] Returning ${cached.size} items from cache")
                Result.success(CryptoResult(cached, fromCache = true))
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCryptoById(id: String): Result<Crypto> {
        return try {
            val response = api.getCoinById(id)
            Result.success(response.toDomain())
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}