package com.example.zenithportfolio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.example.zenithportfolio.ui.theme.Accent
import com.example.zenithportfolio.ui.theme.Negative
import com.example.zenithportfolio.ui.theme.Positive
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.zenithportfolio.domain.model.Crypto
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import com.example.zenithportfolio.data.api.CoinGeckoApi
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.lang.Exception


data class CryptoDetailScreen(val crypto: Crypto): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val api: CoinGeckoApi = koinInject()
        var priceHistory by remember { mutableStateOf<List<Double>>(emptyList()) }
        var selectedDays by remember { mutableStateOf(7) }

        LaunchedEffect(crypto.id, selectedDays) {
            priceHistory = emptyList()
            withContext(Dispatchers.IO){
                try{
                    kotlinx.coroutines.delay(300) // Evitar rate limit
                    val chart = api.getMarketChart(crypto.id, selectedDays)
                    priceHistory = chart.prices.map { it[1] }
                }catch (e: Exception){
                    println("Error cargando gráfica: ${e.message}")
                }
            }
        }

        CryptoDetailContent(
            crypto = crypto,
            priceHistory = priceHistory,
            selectedDays = selectedDays,
            onDaysChange = { selectedDays = it },
            onBack = { navigator.pop()}
        )
    }

}

@Composable
fun CryptoDetailContent(
    crypto: Crypto,
    priceHistory: List<Double>,
    selectedDays: Int,
    onDaysChange: (Int) -> Unit,
    onBack: () -> Unit
){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .statusBarsPadding()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.offset(x = (-1).dp, y = (-2).dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        AsyncImage(
            model = crypto.imageUrl,
            contentDescription = crypto.name,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = crypto.name,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = crypto.symbol.uppercase(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        {
            Column(
                modifier = Modifier.padding(16.dp)
            )
            {
                Text("Precio actual", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                Text(
                    text = "$${formatPrice(crypto.price)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${if (crypto.changePercent24h >= 0) "+" else ""}${"%.2f".format(crypto.changePercent24h)}% (24h)",
                    color = if (crypto.changePercent24h >= 0) Positive else Negative,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard("Rank", "#${crypto.rank}", Modifier.weight(1f))
                    StatCard("Market Cap", formatMarketCap(crypto.marketCap), Modifier.weight(1f))
                }

            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (selectedDays == 1) "Últimas 24 horas" else "Últimos $selectedDays días",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(1, 7, 30, 90).forEach { days ->
                DayButton(
                    text = if (days == 1) "24h" else "${days}d",
                    selected = selectedDays == days,
                    onClick = { onDaysChange(days) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        key(selectedDays) {
            val maxPoints = when (selectedDays) {
                1 -> 24
                7 -> 42
                30 -> 30
                90 -> 45
                else -> 50
            }
            val sampledPrices = if (priceHistory.size > maxPoints) {
                val step = priceHistory.size / maxPoints
                priceHistory.filterIndexed { index, _ -> index % step == 0 }
            } else {
                priceHistory
            }
            PriceChart(
                prices = sampledPrices,
                days = selectedDays,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatPrice(price: Double): String {
    return if (price >= 1) "%,.2f".format(price) else "%.6f".format(price)
}

private fun formatMarketCap(marketCap: Long): String {
    return when {
        marketCap >= 1_000_000_000_000 -> "${"%.1f".format(marketCap / 1_000_000_000_000.0)}T"
        marketCap >= 1_000_000_000 -> "${"%.1f".format(marketCap / 1_000_000_000.0)}B"
        marketCap >= 1_000_000 -> "${"%.1f".format(marketCap / 1_000_000.0)}M"
        else -> marketCap.toString()
    }
}


@Composable
fun PriceChart(prices: List<Double>, days: Int, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val initialZoom = Zoom.Content

    LaunchedEffect(prices) {
        if (prices.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries { series(prices) }
            }
        }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(Accent))
                        )
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberTextComponent(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    valueFormatter = { _, value, _ ->
                        if (value >= 1000) "$${(value / 1000).toInt()}k"
                        else if (value >= 1) "$${value.toInt()}"
                        else "$%.4f".format(value)
                    }
                )
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(
                zoomEnabled = true,
                initialZoom = initialZoom
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

@Composable
fun DayButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Accent else MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}