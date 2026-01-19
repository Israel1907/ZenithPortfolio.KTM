import SwiftUI
import Shared

struct CryptoDetailView: View {
    let crypto: Crypto
    @State private var priceHistory: [Double] = []
    @State private var selectedDays: Int = 7
    @State private var isLoading: Bool = false
    @Environment(\.dismiss) private var dismiss

    private let chartFetcher = ChartFetcher()

    var body: some View {
        ZStack {
            Color(hex: "0D0D0D").ignoresSafeArea()

            ScrollView {
                VStack(spacing: 16) {
                    // Header
                    HStack {
                        Button(action: { dismiss() }) {
                            Circle()
                                .fill(Color(hex: "1A1A1A"))
                                .frame(width: 40, height: 40)
                                .overlay(
                                    Text("‹")
                                        .foregroundColor(.white)
                                        .font(.title)
                                        .offset(x: -1, y: -2)
                                )
                        }
                        Spacer()
                    }

                    // Crypto info
                    AsyncImage(url: URL(string: crypto.imageUrl)) { image in
                        image.resizable()
                    } placeholder: {
                        Circle().fill(Color.gray)
                    }
                    .frame(width: 100, height: 100)
                    .clipShape(Circle())

                    Text(crypto.name)
                        .foregroundColor(.white)
                        .font(.title)
                        .fontWeight(.bold)

                    Text(crypto.symbol.uppercased())
                        .foregroundColor(.gray)

                    // Price card
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Precio actual")
                            .foregroundColor(.gray)
                            .font(.caption)

                        Text("$\(formatPrice(crypto.price))")
                            .foregroundColor(.white)
                            .font(.largeTitle)
                            .fontWeight(.bold)

                        Text("\(crypto.changePercent24h >= 0 ? "+" : "")\(String(format: "%.2f", crypto.changePercent24h))% (24h)")
                            .foregroundColor(crypto.changePercent24h >= 0 ? Color(hex: "00D4AA") : Color(hex: "FF4444"))

                        HStack {
                            StatCard(title: "Rank", value: "#\(crypto.rank)")
                            StatCard(title: "Market Cap", value: formatMarketCap(crypto.marketCap))
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(hex: "1A1A1A"))
                    .cornerRadius(16)

                    // Days selector
                    Text(selectedDays == 1 ? "Últimas 24 horas" : "Últimos \(selectedDays) días")
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    HStack {
                        ForEach([1, 7, 30, 90], id: \.self) { days in
                            DayButton(
                                text: days == 1 ? "24h" : "\(days)d",
                                selected: selectedDays == days
                            ) {
                                selectedDays = days
                                loadChart()
                            }
                            .disabled(isLoading)
                            .opacity(isLoading && selectedDays != days ? 0.5 : 1.0)
                        }
                    }

                    // Chart
                    if isLoading {
                        ProgressView()
                            .tint(Color(hex: "00D4AA"))
                            .frame(height: 200)
                    } else if !priceHistory.isEmpty {
                        ChartView(prices: priceHistory)
                            .frame(height: 200)
                            .background(Color(hex: "1A1A1A"))
                            .cornerRadius(16)
                    }
                }
                .padding()
            }
        }
        .navigationBarHidden(true)
        .onAppear { loadChart() }
    }

    func loadChart() {
        guard !isLoading else { return }

        isLoading = true
        priceHistory = []

        chartFetcher.fetchChart(
            cryptoId: crypto.id,
            days: Int32(selectedDays),
            onSuccess: { prices in
                self.priceHistory = prices.map { ($0 as! NSNumber).doubleValue }
                self.isLoading = false
            },
            onError: { error in
                print("Error loading chart: \(error)")
                self.isLoading = false
            }
        )
    }

    func formatPrice(_ price: Double) -> String {
        price >= 1 ? String(format: "%.2f", price) : String(format: "%.6f", price)
    }

    func formatMarketCap(_ cap: Int64) -> String {
        if cap >= 1_000_000_000_000 {
            return String(format: "%.1fT", Double(cap) / 1_000_000_000_000)
        } else if cap >= 1_000_000_000 {
            return String(format: "%.1fB", Double(cap) / 1_000_000_000)
        } else if cap >= 1_000_000 {
            return String(format: "%.1fM", Double(cap) / 1_000_000)
        }
        return "\(cap)"
    }
}

struct StatCard: View {
    let title: String
    let value: String

    var body: some View {
        VStack {
            Text(title)
                .foregroundColor(.gray)
                .font(.caption)
            Text(value)
                .foregroundColor(.white)
                .fontWeight(.bold)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(Color(hex: "1A1A1A"))
        .cornerRadius(12)
    }
}

struct DayButton: View {
    let text: String
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(text)
                .foregroundColor(selected ? .black : .white)
                .fontWeight(.medium)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(selected ? Color(hex: "00D4AA") : Color(hex: "1A1A1A"))
                .cornerRadius(8)
        }
    }
}

struct ChartView: View {
    let prices: [Double]

    var minPrice: Double { prices.min() ?? 0 }
    var maxPrice: Double { prices.max() ?? 1 }

    var yAxisValues: [Double] {
        let range = maxPrice - minPrice
        return (0...4).map { minPrice + (range * Double($0) / 4.0) }.reversed()
    }

    var body: some View {
        HStack(alignment: .top, spacing: 4) {
            // Y-axis labels (5 values)
            VStack {
                ForEach(yAxisValues, id: \.self) { value in
                    Text(formatYValue(value))
                    if value != yAxisValues.last {
                        Spacer()
                    }
                }
            }
            .font(.system(size: 10))
            .foregroundColor(.gray)
            .frame(width: 50, alignment: .trailing)

            // Chart
            GeometryReader { geometry in
                let range = max(maxPrice - minPrice, 0.0001)

                Path { path in
                    for (index, price) in prices.enumerated() {
                        let x = geometry.size.width * CGFloat(index) / CGFloat(max(prices.count - 1, 1))
                        let y = geometry.size.height * (1 - CGFloat((price - minPrice) / range))

                        if index == 0 {
                            path.move(to: CGPoint(x: x, y: y))
                        } else {
                            path.addLine(to: CGPoint(x: x, y: y))
                        }
                    }
                }
                .stroke(Color(hex: "00D4AA"), lineWidth: 2)
            }
        }
        .padding()
    }

    func formatYValue(_ value: Double) -> String {
        if value >= 1000 {
            return String(format: "%.0f", value)
        } else if value >= 1 {
            return String(format: "%.2f", value)
        } else {
            return String(format: "%.4f", value)
        }
    }
}
