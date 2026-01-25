package com.example.zenithportfolio.presentation.crypto

sealed class CryptoEffect {
    data class ShowError(val message: String): CryptoEffect()
    data class NavigateToDetail(val cryptoId: String): CryptoEffect()
    data class ShowToast(val message: String): CryptoEffect()
    object ShowCacheWarning : CryptoEffect()
}