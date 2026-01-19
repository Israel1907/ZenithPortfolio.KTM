# ZenithPortfolio

A cryptocurrency portfolio tracker built with Kotlin Multiplatform (KMP), featuring native UI for both Android and iOS.

## Tech Stack

**Shared (Kotlin Multiplatform)**
- Ktor - HTTP client
- Kotlinx Serialization - JSON parsing
- SQLDelight - Local persistence
- Coroutines + StateFlow - Async & state management
- MVI Architecture

**Android**
- Jetpack Compose
- Vico - Charts
- Koin - Dependency Injection
- Voyager - Navigation

**iOS**
- SwiftUI
- Native chart implementation

## Features
- Real-time crypto prices from CoinGecko API
- Search cryptocurrencies
- Favorites with local persistence
- Price history charts (24h, 7d, 30d, 90d)
- Pull-to-refresh
- Dark theme
