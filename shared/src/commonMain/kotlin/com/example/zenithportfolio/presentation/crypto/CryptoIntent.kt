package com.example.zenithportfolio.presentation.crypto

sealed class CryptoIntent{
    object LoadCryptos: CryptoIntent()
    object Refresh: CryptoIntent()
    data class SearchCrypto(val query: String): CryptoIntent()
    data class ToggleFavorite(val  cryptoId: String): CryptoIntent()
    data class SelectCrypto(val cryptoId: String): CryptoIntent()
}