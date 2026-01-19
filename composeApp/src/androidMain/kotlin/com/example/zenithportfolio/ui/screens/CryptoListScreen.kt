package com.example.zenithportfolio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.zenithportfolio.domain.model.Crypto
import com.example.zenithportfolio.presentation.crypto.CryptoIntent
import com.example.zenithportfolio.presentation.crypto.CryptoState
import com.example.zenithportfolio.presentation.crypto.CryptoViewModel
import org.koin.compose.koinInject
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.statusBarsPadding
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class CryptoListScreen: Screen{

    @Composable
    override fun Content(){
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: CryptoViewModel = koinInject()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(CryptoIntent.LoadCryptos)
        }

        CryptoListContent(
            state = state,
            onRefresh = { viewModel.onIntent(CryptoIntent.Refresh) },
            onCryptoClick = { cryptoId ->
                state.cryptos.find { it.id == cryptoId }?.let { crypto ->
                    navigator.push(CryptoDetailScreen(crypto))
                }
            },
            onFavoriteClick = { viewModel.onIntent(CryptoIntent.ToggleFavorite(it)) },
            onSearch = { viewModel.onIntent(CryptoIntent.SearchCrypto(it)) }
        )
    }
}


@Composable
fun CryptoListContent(
    state: CryptoState,
    onRefresh: () -> Unit,
    onCryptoClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onSearch:(String) -> Unit,
){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF0D0D0D))
    ){
        when{
            state.isLoading ->{
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF00D4AA)
                )
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "Error: ${state.error}",
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh){
                        Text("Reintentar")
                    }
                }
            }
            else ->{
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                        .statusBarsPadding()) {
                        TextField(
                            value = state.searchQuery,
                            onValueChange = onSearch,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Buscar crypto...", color = Color.Gray) },
                            leadingIcon = { Text("🔍", fontSize = 18.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1A1A1A),
                                unfocusedContainerColor = Color(0xFF1A1A1A),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFF00D4AA),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        )
                        {
                            items(state.filteredCryptos){ crypto ->
                                CryptoCard(
                                    crypto = crypto,
                                    isFavorite = crypto.id in state.favorites,
                                    onClick = {onCryptoClick(crypto.id)},
                                    onFavoriteClick = { onFavoriteClick(crypto.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CryptoCard(
    crypto: Crypto,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{onClick()},
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = crypto.imageUrl,
                contentDescription = crypto.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)){
                Text(
                    text = crypto.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = crypto.symbol,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Column(horizontalAlignment = Alignment.End){
                Text(
                    text = "$${formatPrice(crypto.price)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${if (crypto.changePercent24h >= 0) "+" else ""}${"%.2f".format(crypto.changePercent24h)}%",
                    color = if (crypto.changePercent24h >= 0) Color(0xFF00D4AA) else Color(0xFFFF4444),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isFavorite) "⭐" else "☆",
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { onFavoriteClick() }
                )
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return if (price >= 1) {
        "%,.2f".format(price)
    } else {
        "%.6f".format(price)
    }
}