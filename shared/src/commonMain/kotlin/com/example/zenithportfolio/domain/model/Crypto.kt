package com.example.zenithportfolio.domain.model


data class Crypto(
    val id: String,
    val name: String,
    val symbol: String,
    val price: Double,
    val changePercent24h: Double,
    val imageUrl: String,
    val marketCap: Long,
    val rank: Int
)