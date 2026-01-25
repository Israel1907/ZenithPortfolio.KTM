package com.example.zenithportfolio.domain.repository

import com.example.zenithportfolio.data.repository.CryptoResult
import com.example.zenithportfolio.domain.model.Crypto

interface CryptoRepository {
    suspend fun getCryptos(): Result<CryptoResult>
    suspend fun getCryptoById(id: String): Result<Crypto>
}