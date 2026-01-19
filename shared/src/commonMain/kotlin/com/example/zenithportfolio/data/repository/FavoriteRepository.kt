package com.example.zenithportfolio.data.repository

import com.example.zenithportfolio.db.FavoriteQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class FavoriteRepository(private val queries: FavoriteQueries) {

    suspend fun getAllFavorites(): Set<String> = withContext(Dispatchers.IO) {
        queries.selectAll().executeAsList().toSet()
    }

    suspend fun addFavorite(cryptoId: String) = withContext(Dispatchers.IO) {
        queries.insert(cryptoId)
    }

    suspend fun removeFavorite(cryptoId: String) = withContext(Dispatchers.IO) {
        queries.delete(cryptoId)
    }
}

