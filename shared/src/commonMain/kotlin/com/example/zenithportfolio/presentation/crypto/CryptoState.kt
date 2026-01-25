package com.example.zenithportfolio.presentation.crypto

import com.example.zenithportfolio.domain.model.Crypto

data class CryptoState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val cryptos: List<Crypto> = emptyList(),
    val filteredCryptos: List<Crypto> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val searchQuery: String = "",
    val error: String? = null,
    val fromCache: Boolean = false
)