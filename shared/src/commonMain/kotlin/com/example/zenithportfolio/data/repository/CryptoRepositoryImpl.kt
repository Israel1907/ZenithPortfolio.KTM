package com.example.zenithportfolio.data.repository

import com.example.zenithportfolio.data.api.CoinGeckoApi
import com.example.zenithportfolio.data.dto.toDomain
import com.example.zenithportfolio.domain.model.Crypto
import com.example.zenithportfolio.domain.repository.CryptoRepository

class CryptoRepositoryImpl(
    private val api: CoinGeckoApi
): CryptoRepository{

    override suspend fun getCryptos(): Result<List<Crypto>> {
        return try{
            println("[Repository] Calling CoinGecko API...")
            val response = api.getMarkets()
            println("[Repository] Got ${response.size} items from API")
            Result.success(response.map { it.toDomain() })
        }catch (e: Exception){
            println("[Repository] API Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
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