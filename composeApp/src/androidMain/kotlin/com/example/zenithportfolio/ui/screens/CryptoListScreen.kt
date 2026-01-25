package com.example.zenithportfolio.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import com.example.zenithportfolio.ui.theme.Accent
import com.example.zenithportfolio.ui.theme.Negative
import com.example.zenithportfolio.ui.theme.Positive
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
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.zenithportfolio.presentation.crypto.CryptoEffect

class CryptoListScreen: Screen{

    @Composable
    override fun Content(){
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: CryptoViewModel = koinInject()
        val state by viewModel.state.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is CryptoEffect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                    }
                    is CryptoEffect.ShowError -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
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
        .background(MaterialTheme.colorScheme.background)
    ){
        when{
            state.isLoading ->{
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Accent
                )
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "Error: ${state.error}",
                        color = Negative
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
                            placeholder = { Text("Buscar crypto...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            leadingIcon = { Text("🔍", fontSize = 18.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = Accent,
                                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
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
            containerColor = MaterialTheme.colorScheme.surface
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
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = crypto.symbol,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            Column(horizontalAlignment = Alignment.End){
                Text(
                    text = "$${formatPrice(crypto.price)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${if (crypto.changePercent24h >= 0) "+" else ""}${"%.2f".format(crypto.changePercent24h)}%",
                    color = if (crypto.changePercent24h >= 0) Positive else Negative,
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