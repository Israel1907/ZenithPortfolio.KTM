package com.example.zenithportfolio

import androidx.compose.runtime.Composable
import com.example.zenithportfolio.ui.screens.CryptoListScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator


@Composable
@Preview
fun App() {
    Navigator(CryptoListScreen())
}
