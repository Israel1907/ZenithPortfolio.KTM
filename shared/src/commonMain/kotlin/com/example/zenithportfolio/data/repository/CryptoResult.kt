package com.example.zenithportfolio.data.repository

import com.example.zenithportfolio.domain.model.Crypto

data class CryptoResult(
    val cryptos: List<Crypto>,
    val fromCache: Boolean
)
