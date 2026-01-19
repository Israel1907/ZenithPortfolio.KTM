package com.example.zenithportfolio.domain.repository

import com.example.zenithportfolio.domain.model.Crypto

interface CryptoRepository {
    suspend fun getCryptos(): Result<List<Crypto>>
    suspend fun getCryptoById(id: String): Result<Crypto>
}