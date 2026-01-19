import Foundation
import Shared

@MainActor
class CryptoViewModelWrapper: ObservableObject {
    private let viewModel: CryptoViewModel
    private var timer: Timer?
    
    @Published var cryptos: [Crypto] = []
    @Published var isLoading: Bool = true
    @Published var error: String? = nil
    @Published var searchQuery: String = ""
    @Published var favorites: Set<String> = []
    
    init() {
        self.viewModel = IosHelper.shared.createViewModel()
        startObserving()
        loadCryptos()
    }
    
    deinit {
        timer?.invalidate()
    }
    
    func loadCryptos() {
        viewModel.onIntent(intent: CryptoIntent.LoadCryptos())
    }
    
    func refresh() {
        viewModel.onIntent(intent: CryptoIntent.Refresh())
    }
    
    func search(query: String) {
        searchQuery = query
        viewModel.onIntent(intent: CryptoIntent.SearchCrypto(query: query))
    }
    
    func toggleFavorite(cryptoId: String) {
        viewModel.onIntent(intent: CryptoIntent.ToggleFavorite(cryptoId: cryptoId))
    }
    
    private func startObserving() {
        timer = Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { [weak self] _ in
            Task { @MainActor in
                self?.updateState()
            }
        }
    }
    
    private func updateState() {
        guard let state = viewModel.state.value as? CryptoState else { return }
        self.cryptos = state.filteredCryptos
        self.isLoading = state.isLoading
        self.error = state.error
        
        // Convertir Set de Kotlin a Set de Swift
        var swiftFavorites = Set<String>()
        for fav in state.favorites {
            if let favString = fav as? String {
                swiftFavorites.insert(favString)
            }
        }
        self.favorites = swiftFavorites
    }
}
