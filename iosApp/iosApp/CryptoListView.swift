//
//  CryptoListView.swift
//  
//
//  Created by Joe Israel Armijos on 18/1/26.
//

import SwiftUI
import Shared

struct CryptoListView: View {
    @StateObject private var viewModel = CryptoViewModelWrapper()
    @State private var selectedCrypto: Crypto? = nil

    var body: some View {
        NavigationStack {
            ZStack {
                Color(hex: "0D0D0D").ignoresSafeArea()

                if viewModel.isLoading {
                    ProgressView()
                        .tint(Color(hex: "00D4AA"))
                } else if let error = viewModel.error {
                    VStack {
                        Text("Error: \(error)")
                            .foregroundColor(.red)
                        Button("Reintentar") {
                            viewModel.loadCryptos()
                        }
                        .foregroundColor(Color(hex: "00D4AA"))
                    }
                } else {
                    VStack(spacing: 0) {
                        // Search bar
                        HStack {
                            Text("🔍")
                            TextField("Buscar crypto...", text: $viewModel.searchQuery)
                                .foregroundColor(.white)
                                .onChange(of: viewModel.searchQuery) { newValue in
                                    viewModel.search(query: newValue)
                                }
                        }
                        .padding()
                        .background(Color(hex: "1A1A1A"))
                        .cornerRadius(12)
                        .padding()

                        // List
                        List(viewModel.cryptos, id: \.id) { crypto in
                            CryptoRow(
                                crypto: crypto,
                                isFavorite: viewModel.favorites.contains(crypto.id),
                                onFavoriteToggle: {
                                    viewModel.toggleFavorite(cryptoId: crypto.id)
                                }
                            )
                            .listRowBackground(Color(hex: "0D0D0D"))
                            .listRowSeparator(.hidden)
                            .listRowInsets(EdgeInsets(top: 4, leading: 0, bottom: 4, trailing: 0))
                            .onTapGesture {
                                selectedCrypto = crypto
                            }
                        }
                        .listStyle(.plain)
                        .refreshable {
                            viewModel.refresh()
                        }
                    }
                }
            }
            .navigationDestination(item: $selectedCrypto) { crypto in
                CryptoDetailView(crypto: crypto)
            }
        }
    }
}

struct CryptoRow: View {
    let crypto: Crypto
    let isFavorite: Bool
    let onFavoriteToggle: () -> Void

    var body: some View {
        HStack {
            AsyncImage(url: URL(string: crypto.imageUrl)) { image in
                image.resizable()
            } placeholder: {
                Circle().fill(Color.gray)
            }
            .frame(width: 48, height: 48)
            .clipShape(Circle())

            VStack(alignment: .leading) {
                Text(crypto.name)
                    .foregroundColor(.white)
                    .fontWeight(.bold)
                Text(crypto.symbol.uppercased())
                    .foregroundColor(.gray)
                    .font(.caption)
            }

            Spacer()

            VStack(alignment: .trailing) {
                Text("$\(formatPrice(crypto.price))")
                    .foregroundColor(.white)
                    .fontWeight(.bold)
                Text("\(crypto.changePercent24h >= 0 ? "+" : "")\(String(format: "%.2f", crypto.changePercent24h))%")
                    .foregroundColor(crypto.changePercent24h >= 0 ? Color(hex: "00D4AA") : Color(hex: "FF4444"))
                    .font(.caption)
                Button(action: onFavoriteToggle) {
                    Text(isFavorite ? "⭐" : "☆")
                        .font(.title2)
                }
            }

           
        }
        .padding()
        .background(Color(hex: "1A1A1A"))
        .cornerRadius(16)
        .padding(.horizontal, 6)
    }

    func formatPrice(_ price: Double) -> String {
        if price >= 1 {
            return String(format: "%.2f", price)
        } else {
            return String(format: "%.6f", price)
        }
    }
}

// Helper para colores hex
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(.sRGB, red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255, opacity: Double(a) / 255)
    }
}


