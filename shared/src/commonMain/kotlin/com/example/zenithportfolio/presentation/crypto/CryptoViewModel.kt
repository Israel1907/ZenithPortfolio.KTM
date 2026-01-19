package com.example.zenithportfolio.presentation.crypto

import com.example.zenithportfolio.domain.repository.CryptoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.zenithportfolio.data.repository.FavoriteRepository

class CryptoViewModel(
    private val repository: CryptoRepository,
    private val favoriteRepository: FavoriteRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(CryptoState())
    val state: StateFlow<CryptoState> = _state.asStateFlow()

    private val _effect = Channel<CryptoEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadFavorites()
    }
    fun onIntent(intent: CryptoIntent) {
        when (intent) {
            is CryptoIntent.LoadCryptos -> loadCryptos()
            is CryptoIntent.Refresh -> refresh()
            is CryptoIntent.SearchCrypto -> search(intent.query)
            is CryptoIntent.ToggleFavorite -> toggleFavorite(intent.cryptoId)
            is CryptoIntent.SelectCrypto -> selectCrypto(intent.cryptoId)
        }
    }

    private fun loadFavorites(){
        scope.launch {
            val favorites = favoriteRepository.getAllFavorites()
            _state.update { it.copy(favorites = favorites) }
        }
    }

    private fun loadCryptos() {
        scope.launch {
            println("[CryptoViewModel] Loading cryptos...")
            _state.update { it.copy(isLoading = true, error = null) }

            repository.getCryptos()
                .onSuccess { cryptos ->
                    println("[CryptoViewModel] Loaded ${cryptos.size} cryptos")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            cryptos = cryptos,
                            filteredCryptos = cryptos
                        )
                    }
                }
                .onFailure { e ->
                    println("[CryptoViewModel] Error: ${e.message}")
                    e.printStackTrace()
                    _state.update { it.copy(isLoading = false, error = e.message) }
                    _effect.send(CryptoEffect.ShowError(e.message ?: "Error desconocido"))
                }
        }
    }

    private fun refresh() {
        scope.launch {
            _state.update { it.copy(isRefreshing = true) }

            repository.getCryptos()
                .onSuccess { cryptos ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            cryptos = cryptos,
                            filteredCryptos = filterCryptos(cryptos, it.searchQuery)
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isRefreshing = false) }
                    _effect.send(CryptoEffect.ShowError(e.message ?: "Error al refrescar"))
                }
        }
    }

    private fun search(query: String) {
        _state.update { state ->
            state.copy(
                searchQuery = query,
                filteredCryptos = filterCryptos(state.cryptos, query)
            )
        }
    }

    private fun filterCryptos(cryptos: List<com.example.zenithportfolio.domain.model.Crypto>, query: String): List<com.example.zenithportfolio.domain.model.Crypto> {
        if (query.isBlank()) return cryptos
        return cryptos.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.symbol.contains(query, ignoreCase = true)
        }
    }

    private fun toggleFavorite(cryptoId: String) {
        scope.launch {
            val newFavorites = if (cryptoId in _state.value.favorites) {
                favoriteRepository.removeFavorite(cryptoId)
                _state.value.favorites - cryptoId
            } else {
                favoriteRepository.addFavorite(cryptoId)
                _state.value.favorites + cryptoId
            }
            _state.update { it.copy(favorites = newFavorites) }
            _effect.send(CryptoEffect.ShowToast("Favorito actualizado"))
        }
    }

    private fun selectCrypto(cryptoId: String) {
        scope.launch {
            _effect.send(CryptoEffect.NavigateToDetail(cryptoId))
        }
    }
}

