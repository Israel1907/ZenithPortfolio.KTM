package com.example.zenithportfolio.data.repository

import com.example.zenithportfolio.db.CachedCryptoQueries
import com.example.zenithportfolio.domain.model.Crypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CryptoCache(private val queries: CachedCryptoQueries){
    suspend fun saveCryptos(cryptos: List<Crypto>) =withContext(Dispatchers.IO){
        queries.deleteAll()

        cryptos.forEach { crypto ->
            queries.insertOrReplace(
                id = crypto.id,
                name = crypto.name,
                symbol = crypto.symbol,
                price = crypto.price,
                changePercent24h = crypto.changePercent24h,
                imageUrl = crypto.imageUrl,
                marketCap = crypto.marketCap,
                rank = crypto.rank.toLong()
            )
        }
    }

    suspend fun getCachedCryptos(): List<Crypto> = withContext(Dispatchers.IO){
        queries.selectAll().executeAsList().map { cached ->
            Crypto(
                id = cached.id,
                name = cached.name,
                symbol = cached.symbol,
                price = cached.price,
                changePercent24h = cached.changePercent24h,
                imageUrl = cached.imageUrl,
                marketCap = cached.marketCap,
                rank = cached.rank.toInt()
            )
        }
    }
}