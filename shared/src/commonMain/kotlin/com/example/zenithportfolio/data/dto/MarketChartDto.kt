package com.example.zenithportfolio.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MarketChartDto(
    val prices: List<List<Double>>
)

